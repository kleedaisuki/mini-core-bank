#!/usr/bin/env bash
set -Eeuo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
CLI_BIN="${CLI_BIN:-"$ROOT_DIR/bank"}"
DB_URL="${BANK_DB_URL:-jdbc:postgresql://localhost:5432/mini_core_bank}"
DB_USERNAME="${BANK_DB_USERNAME:-mini_core_bank}"
DB_PASSWORD="${BANK_DB_PASSWORD:-mini_core_bank}"
PSQL_URL="${DB_URL#jdbc:}"

RUN_ID="${ONE_SHOT_RUN_ID:-$(date -u +%Y%m%d%H%M%S)-$$}"
ID_NUMBER="OSH-${RUN_ID}"
ACCOUNT_NO="OS${RUN_ID//[^0-9]/}"
MOBILE="+8529$(printf '%07d' "$((RANDOM % 10000000))")"
CUSTOMER_ID=""
ACCOUNT_ID=""

run_cli() {
    "$CLI_BIN" "$@"
}

cleanup() {
    PGPASSWORD="$DB_PASSWORD" psql "$PSQL_URL" \
        -U "$DB_USERNAME" \
        -v ON_ERROR_STOP=1 \
        -v account_id="$ACCOUNT_ID" \
        -v account_no="$ACCOUNT_NO" \
        -v customer_id="$CUSTOMER_ID" \
        -v id_number="$ID_NUMBER" \
        >/dev/null <<'SQL' || true
DELETE FROM savings_account
WHERE account_id = :'account_id';

DELETE FROM account
WHERE account_id = :'account_id'
   OR account_no = :'account_no';

DELETE FROM customer
WHERE customer_id = :'customer_id'
   OR (
        id_type = 'PASSPORT'
        AND id_number = :'id_number'
        AND issuing_region = 'OSHTEST'
   );
SQL
}

require_file() {
    if [[ ! -f "$1" ]]; then
        echo "missing required file: $1" >&2
        return 1
    fi
}

require_contains() {
    local haystack="$1"
    local needle="$2"
    if [[ "$haystack" != *"$needle"* ]]; then
        echo "expected output to contain: $needle" >&2
        echo "$haystack" >&2
        return 1
    fi
}

trap cleanup EXIT

require_file "$CLI_BIN"

help_output="$(run_cli help gui)"
require_contains "$help_output" "Launch the desktop GUI"

register_output="$(run_cli customer register \
    --id-type PASSPORT \
    --id-number "$ID_NUMBER" \
    --issuing-region OSHTEST \
    --mobile-phone "$MOBILE" \
    --residential-address "One-shot test address" \
    --mailing-address "One-shot test address" \
    --is-us-tax-resident false)"
CUSTOMER_ID="$(printf '%s\n' "$register_output" | sed -n 's/.*customer_id=\([^ ]*\).*/\1/p' | tail -n 1)"
if [[ -z "$CUSTOMER_ID" ]]; then
    echo "failed to parse customer_id" >&2
    echo "$register_output" >&2
    exit 1
fi

show_customer_output="$(run_cli customer show --customer-id "$CUSTOMER_ID")"
require_contains "$show_customer_output" "customer_id=$CUSTOMER_ID"
require_contains "$show_customer_output" "id_number=$ID_NUMBER"

open_account_output="$(run_cli account open-savings \
    --customer-id "$CUSTOMER_ID" \
    --account-no "$ACCOUNT_NO")"
ACCOUNT_ID="$(printf '%s\n' "$open_account_output" | awk -F, '/^[0-9a-fA-F-]+,/ {print $1; exit}')"
if [[ -z "$ACCOUNT_ID" ]]; then
    echo "failed to parse account_id" >&2
    echo "$open_account_output" >&2
    exit 1
fi

show_account_output="$(run_cli account show --account-no "$ACCOUNT_NO")"
require_contains "$show_account_output" "$ACCOUNT_ID"
require_contains "$show_account_output" "$CUSTOMER_ID"
require_contains "$show_account_output" "$ACCOUNT_NO"

echo "one-shot smoke test passed"
