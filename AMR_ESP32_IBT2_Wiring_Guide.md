# ESP32 DevKit V1 + Dual IBT-2 Basic Wiring Summary

## 1. Robot Chassis Dimensions
- **Body**: 230 mm wide × 380 mm long × 300 mm high
- **Camera Mount Height**: ~330 mm
- **Tires**: 120 mm diameter, 350 mm sideways stance, 280 mm front-rear wheelbase
- **Motor Layout**: Front-Left + Rear-Left in parallel (Left IBT-2); Front-Right + Rear-Right in parallel (Right IBT-2)

## 2. D-Coded ESP32 Pinout
| ESP32 Pin | Left IBT-2 | Right IBT-2 | Role |
| :--- | :--- | :--- | :--- |
| **D25** | `RPWM` | — | Left forward PWM |
| **D26** | `LPWM` | — | Left reverse PWM |
| **D27** | `R_EN` + `L_EN` | — | Left enable |
| **D32** | — | `RPWM` | Right forward PWM |
| **D33** | — | `LPWM` | Right reverse PWM |
| **D14** | — | `R_EN` + `L_EN` | Right enable |
| **VIN (5V)** | `VCC` | `VCC` | 5V logic power (never connect 12V to `VCC`) |
| **GND** | `GND` | `GND` | Common ground bus |

- **12V Motor Power**: Connect battery (+) via fuse to `B+` screw terminals and battery (-) to `B-` / common ground.
- **Safety Cutoff**: On stop or communication loss, the controller cuts H-bridge drive signals (safety interlock principle presented in ECSA-13 paper).
