# Real Hardware Integration Plan

## Overview
This implementation plan addresses the real hardware integration requirements for **VISION X / GREENCOLD**, ensuring that telemetry, health checks, and actuator controls (fans/compressor) interact correctly with the Raspberry Pi backend without falling back to silent dummy data.

## Planned Steps
1. **Android DTO Models (`RaspberryPiModels.kt`)**: Refine models to handle unavailable sensor readings (`null` or explicit status fields) and match actual Python API response schemas.
2. **Android API Client (`RaspberryPiApi.kt`)**: Ensure endpoints (`/api/live`, `/api/health`, `/api/control/cooling`, `/api/control/fans`) are correctly typed with error propagation.
3. **Android Repository (`ColdStorageRepository.kt`)**: Enforce strict error propagation and unavailable states when Raspberry Pi mode is active, avoiding mock fallback.
4. **Testing & Verification**: Run gradle build and verify compilation integrity.
