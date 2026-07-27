# ST10F276E Firmware Preparation, Markup, and Analysis Guide for Ghidra

---

## 1. Step-by-Step Markup and Analysis Process

### Step 1. Environment Setup and SFR Mapping
1. Copy the `ImportST10F276E_SFR.java` file into your Ghidra scripts directory (e.g., `C:\Users\<User>\ghidra_scripts\`).
2. Open **Script Manager** in Ghidra.
3. Click the **Refresh Script List** icon (two yellow curved arrows in the upper-right corner) to clear Ghidra's compiled class cache.
4. Locate `ImportST10F276E_SFR.java` in the list and click **Run**.
   * *What the script does:* Creates the `SFR` (`0xFE00`) and `ESFR` (`0xF000`) memory blocks, sets the default page register context (`DPP0=0`, `DPP1=1`, `DPP2=2`, `DPP3=3`) across the entire memory space, and maps the ST10 peripheral register labels.

### Step 2. Interrupt Vector (ISR) Marking
1. In **Script Manager**, navigate to the `C166` category.
2. Find and run the built-in script **`AddISRLabels.java`**.
   * *Result:* The script scans the `0x0000`–`0x0200` range, creates labels for all 66 interrupt vectors, and identifies the actual `RESET` entry point (at address `0x000200` in our case).

### Step 3. Navigate to the RESET Start Vector
1. Press **`G`** (*Go to*) and enter address **`0x200`** (or `0x000200`).

### Step 4. Mark the First Function (`RESET_handler`)
1. Ensure the cursor is positioned at address `0x000200`.
2. If raw bytes (`??`) are shown at this address:
   * Press **`D`** (*Disassemble*). The instruction will expand into assembly code (e.g., `calla cc_UC, FUN_000204`).
3. Once the bytes are converted into assembly, press **`F`** (*Create Function*). Ghidra will create the `RESET_handler` function.

> **Important:** Do not press `F` while the cursor is on raw, undisassembled bytes (`??`), or Ghidra will attempt to interpret them as floating-point data (*Float*). If this happens, press **`C`** (*Clear*) and repeat the **`D`** $\rightarrow$ **`F`** sequence.

### Step 5. Run Initial Auto-Analysis
1. Press **`F5`** (or go to **Analysis → Auto Analyze...**).
2. Keep the default settings and click **Analyze**.
3. Wait for the analyzer to complete (indicated in the bottom-right corner).

---

## 2. Verifying Disassembly Correctness

After auto-analysis completes, perform the following 5 verification steps:

### 1. Overview Bar Inspection
Examine the thin vertical bar on the right side of the **Listing** window:
* **Green:** Successfully analyzed code.
* **Blue / Cyan:** Data and tables.
* **Gray / Red:** Unmarked bytes (`undefined`) or errors.
* *Goal:* Ensure there are no large gray gaps within the main firmware region (excluding empty Flash areas filled with `0xFF` at the end of memory).

### 2. Check the Bookmarks Window for Errors
Open **Window → Bookmarks** and filter by the **Error** type:
* Errors like **`Unable to resolve constructor`** typically occur when Ghidra accidentally attempts to disassemble `switch-case` jump tables or calibration maps.
* To fix them, select the affected bytes and press **`C`** (*Clear*) to revert them to data, or run the built-in script **`C166SwitchOverride.java`** (`Ctrl + Shift + S`).

### 3. Decompiler & DPP Addressing Check
Navigate to the newly created `FUN_000204` function and open the **Decompiler** window:
* **Sign of Success:** C code displays clear register names (`SYSCON`, `DPP0`, `T3CON`, `WDTCON`) and structured RAM variable references (`DAT_00fe00`).
* **Sign of Failure:** References like `*(undefined2 *)(ulong)ram0x10200` indicate a broken or missing `DPP` register context in that code segment.

### 4. Function Tree Inspection (Symbol Tree)
In the **Symbol Tree** panel on the left, expand the **Functions** folder:
* Verify that vector handlers (`RESET_handler`, `ADCINT`, `T1INT`, etc.) are present.
* Spot-check 2–3 functions from the middle of the list to ensure they terminate properly with return instructions (`RETS`, `RETI`, or `RETP`).

### 5. Locating Unassembled Code
If you spot a gray `undefined` block that looks like valid code:
1. Place the cursor at the beginning of the block.
2. Press **`D`** (*Disassemble*).
3. Press **`F`** (*Create Function*) if it represents a standalone function.
