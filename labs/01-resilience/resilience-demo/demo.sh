#!/usr/bin/env bash
# Walks through the circuit breaker lifecycle against a running app (mvn spring-boot:run).
# Usage: ./demo.sh [base-url]
BASE="${1:-http://localhost:8085}"

state()  { curl -s "$BASE/actuator/circuitbreakers" | grep -o '"state":"[A-Z_]*"' | head -1; }
mode()   { curl -s -X POST "$BASE/admin/downstream?mode=$1${2:+&delayMs=$2}" > /dev/null; echo ">> downstream mode = $1 ${2:+(delay ${2}ms)}"; }
pay()    { # one request only: body and "HTTP code / time" are captured from the same call
  out=$(curl -s -w ' |HTTP %{http_code} %{time_total}s' -X POST "$BASE/orders/$1/pay")
  echo "   pay $1 -> ${out##*|}  $(echo "${out%|*}" | grep -o '"status":"[A-Z]*"\|"detail":"[^"]*"\|"error":"[^"]*"' | tr '\n' ' ')"; }
reached(){ echo "   requests that actually reached payment-service: $(curl -s "$BASE/admin/downstream" | grep -o '"callsReceived":[0-9]*')"; }

echo "== 0. healthy: breaker CLOSED"
curl -s -X POST "$BASE/admin/downstream/reset-counter" > /dev/null
mode OK
pay a1; state

echo; echo "== 1. payment-service starts failing (500). Watch it trip after the 5th call."
mode FAIL
for i in 1 2 3 4 5 6; do pay f$i; done
state; reached

echo; echo "== 2. breaker OPEN: calls are rejected instantly and the downstream sees nothing"
for i in 1 2 3; do pay blocked$i; done
reached
echo "   the cart page is unaffected:"; curl -s "$BASE/orders/1/cart"; echo

echo; echo "== 3. payment-service heals; breaker moves to HALF_OPEN after ~10s"
mode OK
echo "   waiting 11s..."; sleep 11; state

echo; echo "== 4. three trial calls succeed -> CLOSED"
for i in 1 2 3; do pay trial$i; done
state

echo; echo "== 5. declined cards are business outcomes: breaker must stay CLOSED"
mode DECLINE
for i in 1 2 3 4 5 6 7 8; do pay d$i; done
state

echo; echo "== 6. slow but 'successful' calls (2s > 1s slow threshold) also trip it"
mode SLOW 2000
for i in 1 2 3 4 5 6; do pay s$i; done
state
mode OK
