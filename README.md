# Ghidra Instructions & Tools for ST10F276E / C166

[![Ghidra](https://img.shields.io/badge/Ghidra-11.x%20%7C%2012.x-blue)](https://ghidra-sre.org/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

Repository containing language specifications, Java scripts, and step-by-step guides for importing, mapping, and analyzing **ST10F276E / ST10F275E (C166 architecture)** ECU firmware in Ghidra.


---

## 🇬🇧 English Version

### 📁 Repository Structure

```text
.
├── language/
│   ├── c166.ldefs             # Language & compiler definitions for C166/Tasking
│   └── st10f276.pspec         # Processor specification (memory maps, SFRs, DPP context)
├── C166SwitchOverride.java    # Script/fix for overriding and mapping switch-case jump tables
├── ImportST10F276E_SFR.java  # Script for automated SFR/ESFR block creation and register labeling
├── ghidra_st10_guide_EN.md   # Step-by-step firmware reverse engineering guide (English)
├── ghidra_st10_guide_RU.md   # Step-by-step firmware reverse engineering guide (Russian)
└── LICENSE                    # Project license
