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

📄 Component Description
🛠️ Processor Specifications (language/)
Configuration files designed for the c166-ghidra-module plugin. Registers support for the ST10Microelectronics ST10F276E / ST10F275E processor profile.

📜 Ghidra Java Scripts
ImportST10F276E_SFR.java — Automatically sets up memory blocks for SFR (0xFE00) and ESFR (0xF000), configures global page register context (DPP0..DPP3), and maps ST10 peripheral register labels.

C166SwitchOverride.java — Auxiliary script for handling switch-case indirect control flows. Includes compatibility patches for recent Ghidra releases (v11.x/v12.x JumpTable API changes).

📖 Reverse Engineering Guides
ghidra_st10_guide_EN.md — Comprehensive English documentation for workspace setup, interrupt vector mapping, script fixes, and disassembly integrity verification.

ghidra_st10_guide_RU.md — Comprehensive Russian documentation covering binary import, script compilation fixes, interrupt vector (ISR) labeling, and DPP addressing validation in the decompiler.

🚀 Quick Start
Copy the files from the language/ directory into your Ghidra C166 language module folder (e.g., c166-ghidra-module/data/languages/).

Place the Java scripts (ImportST10F276E_SFR.java, C166SwitchOverride.java) into your Ghidra scripts directory (e.g., ~/ghidra_scripts/).

Follow the detailed workflow described in ghidra_st10_guide_EN.md.
