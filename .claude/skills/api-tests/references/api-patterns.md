# API Testing Patterns

## 1. Auth Flow
**Use when:** Endpoint requires authentication.
- **Flow:** Missing Header (401) -> Invalid Token (401) -> Expired (401) -> Wrong Role (403) -> Valid (200).
- **Helper:** `AuthHelper.getToken(role)` via `@Step`.
- **Refresh:** Verify token refresh works (Phase 3).

## 2. CRUD
**Use when:** Standard resource lifecycle.
- **Create:** 400 (validation) -> 201 (check fields) -> 409 (duplicate).
- **Read:** 200 (by ID) -> 404 (missing).
- **Update:** 200 (verify change). Verify ONLY changed fields.
- **Delete:** 204 -> Verify GET returns 404.

## 3. Pagination & List
**Use when:** GET returns lists.
- **Checks:**
  - Default params (page 1).
  - `pageSize` limits (request 5 -> get <=5).
  - Navigation (p1 != p2).
  - Empty results (valid 200).
  - Boundary (max size, invalid inputs -> 400).
- **Assert:** `items.size <= pageSize`, `total` consistency.

## 4. Idempotency
**Use when:** RETRY safety (PUT, DELETE, Idempotency-Key).
- **Logic:** Request A -> Response X. Request A (again) -> Response X (Identical).
- **DELETE:** 204 -> 204 (or 404 depending on spec).
- **Key:** POST + Key -> 201. Retry + Same Key -> 200 (Not 201), same body.
