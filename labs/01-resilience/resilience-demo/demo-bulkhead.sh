#!/usr/bin/env bash
# Shows what a bulkhead protects. Needs the app running (mvn spring-boot:run).
# Setup: Tomcat has only 10 request threads, inventory takes 3s, and 10 customers ask for stock at once.
# Then someone opens the cart page, which has nothing to do with inventory.
# Usage: ./demo-bulkhead.sh [base-url]
BASE="${1:-http://localhost:8085}"
DELAY=3000

run_round() {   # $1 = path, $2 = label
  tmp=$(mktemp -d)
  for i in $(seq 1 10); do
    ( curl -s -w ' %{time_total}' "$BASE/orders/$i$1" > "$tmp/$i.out" ) &
  done
  sleep 0.5      # let the 10 requests settle into their threads
  cart=$(curl -s -o /dev/null -w '%{time_total}' "$BASE/orders/1/cart")
  wait
  checked=$(grep -l '"status":"IN_STOCK"' "$tmp"/*.out 2>/dev/null | wc -l | tr -d ' ')
  refused=$(grep -l '"status":"UNKNOWN"' "$tmp"/*.out 2>/dev/null | wc -l | tr -d ' ')
  slowest=$(awk '{print $NF}' "$tmp"/*.out | sort -n | tail -1)
  fastest=$(awk '{print $NF}' "$tmp"/*.out | sort -n | head -1)
  echo "   $2"
  echo "     stock checks that really ran : $checked"
  echo "     stock checks refused at once : $refused"
  echo "     fastest / slowest stock reply: ${fastest}s / ${slowest}s"
  echo "     CART PAGE (unrelated) took   : ${cart}s   <-- the number to watch"
  rm -rf "$tmp"
}

curl -s -X POST "$BASE/admin/inventory?delayMs=$DELAY" > /dev/null
echo ">> inventory is now slow: ${DELAY}ms per call. Tomcat has 10 request threads."
echo
echo "== A. WITHOUT a bulkhead (/stock-unprotected)"
run_round "/stock-unprotected" "10 customers ask for stock"
sleep 4
echo
echo "== B. WITH a bulkhead, max 3 at once (/stock)"
run_round "/stock" "10 customers ask for stock"
sleep 4
curl -s -X POST "$BASE/admin/inventory?delayMs=0" > /dev/null
echo; echo ">> inventory back to normal"
