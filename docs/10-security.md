# Security

```text
Application UID
  → Binder
  → Permission check in system service
  → CarService / custom service
  → VHAL
```

Rules for this project:

- No Binder interface without a permission
- Validate every SET (range, area id, user)
- Do not expose VHAL to third-party apps
- Simulated GPS is not a location permission bypass on a real device — when we switch to `LocationManager`, request the real location permissions and follow UX restriction rules for maps input

Signature / privileged permissions are **platform** concepts. This debug APK is not a privileged `/system/priv-app` module.
