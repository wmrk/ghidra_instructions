# ST10F276E Firmware Preparation, Mapping, and Analysis Guide for Ghidra

---

## 1. Step-by-Step Mapping and Analysis Procedure

### Step 1. File Preparation, Binary Import, and Architecture Selection
1. Navigate to the `languages` folder in the repository and copy the **2 configuration files** (`c166.ldefs` and `st10f276.pspec`) into the corresponding language specifications directory of your `c166-ghidra-module` in Ghidra.
2. In Ghidra, select **File → Import File** (or press `I`) and select your firmware binary file.
3. In the architecture selection window (**Language**), specify the following parameters:
   * **Language:** `ST10Microelectronics ST10F276E / ST10F275E`
   * **Language ID:** `C166:LE:16:ST10F276E`
   * **Compiler:** `tasking`

### Step 2. Environment Setup and SFR Mapping
1. Copy `ImportST10F276E_SFR.java` to your Ghidra scripts directory (e.g., `C:\Users\<User>\ghidra_scripts\`).
2. Open the **Script Manager** in Ghidra.
3. Click the **Refresh Script List** icon (two yellow circular arrows at the top right) to clear the compiled class cache.
4. Locate `ImportST10F276E_SFR.java` in the list and click **Run**.
   * *Script functionality:* Creates the `SFR` (`0xFE00`) and `ESFR` (`0xF000`) memory blocks, sets the default page register context to `DPP0=0`, `DPP1=1`, `DPP2=2`, `DPP3=3` across the entire address space, and maps the ST10 peripheral register labels.

### Step 3. Interrupt Vector (ISR) Mapping
1. In the **Script Manager**, navigate to the `C166` category.
2. Locate and execute the built-in script **`AddISRLabels.java`***.
   * *Output:* Scans the `0x0000`–`0x0200` region, generates labels for all 66 interrupt vectors, and identifies the entry point `RESET` (at address `0x000200` in this configuration).

### Step 4. Navigation to Start Vector `RESET`
1. Press **`G`** (*Go to*) and enter address **`0x200`** (or `0x000200`).

### Step 5. Initial Function Mapping (`RESET_handler`)
1. Ensure the cursor is positioned at address `0x000200`.
2. If raw unparsed bytes (`??`) are present at this address:
   * Press **`D`** (*Disassemble*) to convert the instruction stream into assembly (e.g., `calla cc_UC, FUN_000204`).
   * Press **`F`** (*Create Function*) once disassembled to generate `RESET_handler`.
3. If the code at this address is **already recognized** as a function (displaying disassembled instructions rather than `??`), no manual intervention is required.

> **Important:** Do not press `F` while the cursor is over undefined bytes (`??`), as Ghidra will interpret the selection as a floating-point data type (`Float`). If this occurs, press **`C`** (*Clear*) and repeat the **`D`** $\rightarrow$ **`F`** sequence.

### Step 6. Initial Auto-Analysis Execution
1. Press **`F5`** (or select **Analysis → Auto Analyze...**).
2. Retain default configuration settings and click **Analyze**.
3. Monitor the status indicator in the bottom-right corner until analysis completes.

---

## 2. Disassembly Integrity Verification

Upon completion of the auto-analysis phase, perform the following 5 verification steps:

### 1. Overview Bar Inspection
Examine the vertical sidebar adjacent to the **Listing** window:
* **Green:** Successfully analyzed executable code.
* **Blue/Cyan:** Tables, constants, and data sections.
* **Gray/Red:** Unmapped bytes (`undefined`) or disassembly errors.
* *Objective:* Confirm that no extensive unmapped gray regions remain within active flash memory bounds (excluding trailing `0xFF` padding spaces).

### 2. Error Bookmarks Review
Navigate to **Window → Bookmarks** and filter entries by **Error** type:
* **`Unable to resolve constructor`** errors typically indicate incorrect disassembly paths into `switch-case` jump tables or calibration structures.
* To resolve these, select the affected bytes and press **`C`** (*Clear*) to revert them to data, or run **`C166SwitchOverride.java`** (`Ctrl + Shift + S`).

### 3. Decompiler & DPP Addressing Check
Navigate to `FUN_000204` and inspect the **Decompiler** view:
* **Expected Output:** Explicit SFR register references (`SYSCON`, `DPP0`, `T3CON`, `WDTCON`) and correctly resolved RAM variable offsets (`DAT_00fe00`).
* **Anomalous Output:** Dereferenced raw pointers such as `*(undefined2 *)(ulong)ram0x10200` signal invalid `DPP` register context configuration in that segment.

### 4. Function Tree Structure Check
Expand the **Functions** node in the **Symbol Tree** panel:
* Verify vector handler definitions (`RESET_handler`, `ADCINT`, `T1INT`, etc.).
* Sample several functions across the address space and verify proper control flow termination via return instructions (`RETS`, `RETI`, or `RETP`).

### 5. Unparsed Code Identification
If an unanalyzed gray block (`undefined`) containing valid instructions is located:
1. Position the cursor at the entry boundary.
2. Press **`D`** (*Disassemble*).
3. Press **`F`** (*Create Function*) if it constitutes a standalone routine.

---
\* **Note (Compilation Error Mitigation):**  
Executing `AddISRLabels.java` (or related scripts) may trigger a compilation error due to API changes affecting `C166SwitchOverride.java` in recent Ghidra releases (v11/v12 `JumpTable` constructor signature mismatches).

* **Resolution:** Replace `C166SwitchOverride.java` with an updated version, or modify line **376** in a text editor as follows:
  ```java
  // Original:
  JumpTable jumpTable = new JumpTable(switchAddr, targets, true);

  // Updated:
  JumpTable jumpTable = new JumpTable(switchAddr, targets, true, 0);
