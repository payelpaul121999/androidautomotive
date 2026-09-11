# CAN bus (reference)

Production path:

```text
ECU → CAN frame (ID + payload) → gateway / MCU → VHAL parser → properties
```

This increment has **no** CAN stack. Navigation CAN IDs in the original brief (0x101 speed, etc.) belong to a **future vendor sim**, not Compose.

Never parse CAN in the UI process on a real vehicle.
