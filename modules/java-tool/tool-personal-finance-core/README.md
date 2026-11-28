# tool-personal-finance-core

Collects personal finance transactions from several sources, normalizes and de-duplicates them, then hands them to a `FinanceSink` (the "dedicated finance application").

```
FinanceSource ──► TransactionExtractor ──► normalize ──► dedupe ──► categorize ──► FinanceSink
 (file/mail/web)   (tabular | LLM)                       (+ ledger)                (staging JSONL now)
```

## Run

```
java -jar target/tool-personal-finance-core-*-all.jar sync --config storage/finance/finance-config.json [--source id] [--dry-run]
```

Environment variables are listed in the "Personal finance" section of `.env.example`.

## Config (`finance-config.json`)

```json
{
  "sinkId": "staging",
  "layouts": [
    { "id": "sample-bank", "format": "csv", "skipRows": 1, "dateColumn": 0, "dateFormat": "dd/MM/yyyy",
      "descriptionColumn": 1, "amountColumn": 2 }
  ],
  "fileSources": [
    { "id": "main-bank", "kind": "BANK_STATEMENT_FILE", "directory": "main-bank",
      "accountId": "main", "accountName": "Main account", "layoutId": "sample-bank" }
  ],
  "mailSources": [
    { "id": "bank-alerts", "folder": "INBOX", "senders": ["alert@example-bank.com"],
      "accountId": "main", "accountName": "Main account" }
  ],
  "categoryRules": [ { "keyword": "shopee", "category": "Shopping" } ],
  "categories": ["Shopping", "Food", "Transport", "Salary"]
}
```

A layout describes one statement format by 0-based column index: either `amountColumn` (negative = debit) or `debitColumn` + `creditColumn`. File sources without a `layoutId` (PDF, `.eml`) go through the LLM extractor.

## Safety rules

- Amounts returned by the LLM are rejected unless the number appears in the source text (`AmountGuard`); statements with opening/closing balances must reconcile (`BalanceReconciler`) or nothing from them is accepted. Rejections are listed in the sync report.
- The same transaction coming from several sources is matched by bank reference, else by account + direction + amount + date ±1 day; the statement/web record wins over the email alert.
- Every transaction is recorded in a local write ledger right after the sink confirms it, so a crash halfway through a sink run never re-enters rows on the next run.

## TODO

- Bank/wallet specific `StatementLayout`s: needs anonymized sample exports.
- `BankWebScraper` implementations per bank and wiring of `BankWebSource` in `FinanceSyncAssembler`.
- `SeleniumFinanceSink` for the target web app (replaces the staging `JsonlFinanceSink`).
- Upgrade an already written email record when its statement row arrives later (`Deduplicator`).
- Further file formats in `FileDropSource` (`.xls`, `.txt`) once real exports are available.
- Live IMAP integration test (`*LiveIT`), gated on the `FINANCE_IMAP_*` variables.
