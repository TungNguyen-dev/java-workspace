# tool-personal-finance-core

Collects personal finance transactions from several sources, normalizes and de-duplicates them, then hands them to a `FinanceSink` (the "dedicated finance application").

```
FinanceSource ──► TransactionExtractor ──► normalize ──► dedupe ──► categorize ──► FinanceSink
 (file/mail/web)   (tabular | LLM)                       (+ ledger)                (staging JSONL now)
```

## Run

```
java -jar target/tool-personal-finance-core-*-all.jar sync --config storage/finance/finance-config.json [--source id] [--dry-run]
java -jar target/tool-personal-finance-core-*-all.jar note [--config path] [--date yyyy-MM-dd]
```

After every non-dry `sync`, a Markdown note is written per booked day to `<storage>/out/daily/yyyy-MM-dd.md` (income, expense, net, transactions table, expense by category). `note` rebuilds a day's note from the write ledger; both are idempotent.

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
  "appleMailSources": [
    { "id": "bank-mail", "mailboxes": ["INBOX"], "senders": ["alert@example-bank.com"],
      "accountId": "main", "accountName": "Main account" }
  ],
  "categoryRules": [ { "keyword": "shopee", "category": "Shopping" } ],
  "categories": ["Shopping", "Food", "Transport", "Salary"]
}
```

A layout describes one statement format by 0-based column index: either `amountColumn` (negative = debit) or `debitColumn` + `creditColumn`. File sources without a `layoutId` (PDF, `.eml`) go through the LLM extractor.

### Apple Mail source

`appleMailSources` reads the `.emlx` files that the macOS Mail app keeps under `~/Library/Mail/V<n>` (the highest `V<n>` is used; override with `mailRoot` or `FINANCE_APPLE_MAIL_ROOT`). No IMAP credentials are needed, but:

- the terminal/IDE running the tool needs **Full Disk Access** (System Settings > Privacy & Security);
- `senders` is mandatory (substring match on the From address), so unrelated personal mail is never read by the LLM;
- `mailboxes` limits the scan to mailboxes by name (empty = all); the same Message-ID found in several mailboxes is read once;
- mail bodies go through the LLM extractor, so `FINANCE_LLM_PROVIDER` must be set;
- the first run (no cursor yet) parses every mail of the configured senders; later runs only look at files modified since the last sync;
- a mail that Mail has not fully downloaded is a `.partial.emlx`; open it in Mail first if its body or attachments are missing.

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
