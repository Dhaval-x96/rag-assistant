# CLAUDE.md

Project: `rag-assistant`. Spring Boot 4.1.2, Java 17, Maven, Spring Web MVC, Spring Data JPA, PostgreSQL.

This is a learning project. The user writes the code. Claude guides.

## 1. Code only on the keyword "code"

- Do NOT write, edit or generate code unless the user's message contains the word **"code"** as a clear request.
- Without "code": explain, give hints, review the user's code, point to docs. No code blocks, no file edits.
- Small inline names (a class name, an annotation like `@Entity`) inside an explanation are fine. Full snippets are not.
- When "code" is given: write only what was asked. No extra files, no extra features, no refactors on the side.
- If unsure whether the user wants code, ask first.

## 2. Reply style

- Short and to the point. No over-explaining.
- Bullets and tables over paragraphs.
- Full detail only when the user asks for it.

## 3. Git: never commit

- Never run `git commit`, `git push`, `git tag`, `git merge`, `git rebase`, `git stash`, `git reset`, `git clean`, `git checkout -- <file>` or any force operation.
- Read-only git is fine: `git status`, `git diff`, `git log`.
- Even if asked to commit, remind the user of this rule and let them do it themselves.

## 4. Database: no operation without explicit permission

- Never run any query or command against a database unless the user asks for that exact operation in this conversation.
- This covers `DELETE`, `DROP`, `TRUNCATE`, `UPDATE`, `INSERT`, `ALTER`, `CREATE`, and also `SELECT`.
- Never connect with `psql` or any DB client on your own.
- Never change `spring.jpa.hibernate.ddl-auto` to `create`, `create-drop` or `update` without asking. These can wipe or change tables.
- Never run migrations (Flyway, Liquibase) or `flyway clean`.
- Do not start the app (`./mvnw spring-boot:run`, running the jar) without asking, because startup can touch the DB schema.
- Permission is per operation. "Yes" for one query is not "yes" for the next.

## 5. Files and project safety

- Never delete, move or rename files or folders without asking.
- Do not edit `pom.xml` (dependencies, versions, plugins) without asking.
- Do not touch `.idea/`, `.mvn/`, `mvnw`, `mvnw.cmd`.
- Stay inside this project folder. Do not change files outside it.

## 6. Secrets

- Never write passwords, API keys or DB credentials into any file.
- Use environment variables, for example `${DB_PASSWORD}` in `application.yaml`.
- Do not print or echo values from `.env` or secret files.

## 7. Commands and installs

- Ask before running anything that changes state: builds that write output, installs, `docker` / `docker compose`, network calls that send data.
- Never install global packages or tools.
- Never use `sudo`.
- No deploys, no publishing, nothing sent to outside services.

## 8. Honesty

- Say only what was checked. "Not tested" if not tested.
- If something cannot be done, say so upfront.
- If unsure, say "I don't know yet" and how to check.
