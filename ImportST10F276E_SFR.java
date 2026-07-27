// Ghidra script: Auto-creates memory blocks, sets default DPP registers, and adds ST10F276E SFR labels.
// @category ST10

import ghidra.app.script.GhidraScript;
import ghidra.program.model.address.Address;
import ghidra.program.model.lang.Register;
import ghidra.program.model.listing.ProgramContext;
import ghidra.program.model.mem.Memory;
import ghidra.program.model.symbol.SourceType;
import ghidra.program.model.symbol.SymbolTable;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class ImportST10F276E_SFR extends GhidraScript {

    @Override
    public void run() throws Exception {
        int transactionId = currentProgram.startTransaction("Setup ST10F276E Context & SFRs");
        boolean success = false;

        try {
            // 1. Создаем блоки SFR и ESFR в Memory Map (если их еще нет)
            ensureMemoryBlocks();

            // 2. Устанавливаем базовые контекстные регистры DPP0..DPP3 на весь адресный диапазон
            setDefaultDPPContext();

            // 3. Создаем метки регистров
            SymbolTable symbolTable = currentProgram.getSymbolTable();
            Map<Long, String> sfrMap = getST10F276ESfrMap();

            int count = 0;
            for (Map.Entry<Long, String> entry : sfrMap.entrySet()) {
                Address addr = toAddr(entry.getKey());
                if (addr != null) {
                    symbolTable.createLabel(addr, entry.getValue(), SourceType.USER_DEFINED);
                    count++;
                }
            }

            println("[+] Успешно! Заданы DPP0..DPP3 и размечено " + count + " регистров ST10F276E.");
            success = true;
        } catch (Exception e) {
            printerr("Ошибка при выполнении скрипта: " + e.getMessage());
        } finally {
            currentProgram.endTransaction(transactionId, success);
        }
    }

    private void setDefaultDPPContext() {
        ProgramContext context = currentProgram.getProgramContext();
        Address minAddr = currentProgram.getMinAddress();
        Address maxAddr = currentProgram.getMaxAddress();

        setDppValue(context, "DPP0", minAddr, maxAddr, 0);
        setDppValue(context, "DPP1", minAddr, maxAddr, 1);
        setDppValue(context, "DPP2", minAddr, maxAddr, 2);
        setDppValue(context, "DPP3", minAddr, maxAddr, 3);

        println("[*] Установлены дефолтные контекстные регистры: DPP0=0, DPP1=1, DPP2=2, DPP3=3");
    }

    private void setDppValue(ProgramContext context, String regName, Address start, Address end, long value) {
        Register reg = context.getRegister(regName);
        if (reg != null) {
            try {
                context.setValue(reg, start, end, BigInteger.valueOf(value));
            } catch (Exception e) {
                println("Предупреждение: Не удалось установить " + regName + ": " + e.getMessage());
            }
        }
    }

    private void ensureMemoryBlocks() throws Exception {
        Memory memory = currentProgram.getMemory();

        Address esfrAddr = toAddr(0xF000L);
        if (memory.getBlock(esfrAddr) == null) {
            memory.createUninitializedBlock("ESFR", esfrAddr, 0x200L, false);
            println("[*] Создан блок памяти ESFR (0xF000 - 0xF1FF)");
        }

        Address sfrAddr = toAddr(0xFE00L);
        if (memory.getBlock(sfrAddr) == null) {
            memory.createUninitializedBlock("SFR", sfrAddr, 0x200L, false);
            println("[*] Создан блок памяти SFR (0xFE00 - 0xFFFF)");
        }
    }

    private Map<Long, String> getST10F276ESfrMap() {
        Map<Long, String> m = new HashMap<>();

        // === System & CPU Core Registers ===
        m.put(0xFE00L, "DPP0");
        m.put(0xFE02L, "DPP1");
        m.put(0xFE04L, "DPP2");
        m.put(0xFE06L, "DPP3");
        m.put(0xFE10L, "CP");
        m.put(0xFE12L, "SP");
        m.put(0xFE14L, "STKOV");
        m.put(0xFE16L, "STKUN");
        m.put(0xFF10L, "PSW");
        m.put(0xFF12L, "SYSCON");
        m.put(0xF012L, "SYSCON2");
        m.put(0xF014L, "SYSCON3");
        m.put(0xFF1EL, "ODPCON");

        // === Bus Control ===
        m.put(0xFF0CL, "BUSCON0");
        m.put(0xFF0EL, "BUSCON1");
        m.put(0xFF14L, "BUSCON2");
        m.put(0xFF16L, "BUSCON3");
        m.put(0xFF18L, "BUSCON4");

        // === I/O Ports ===
        m.put(0xFF00L, "P0L");
        m.put(0xFF02L, "P0H");
        m.put(0xFF04L, "P1L");
        m.put(0xFF06L, "P1H");
        m.put(0xFF08L, "P2");
        m.put(0xFF0CL, "P3");
        m.put(0xFF0EL, "P4");
        m.put(0xFFA2L, "P5");

        // === Timers ===
        m.put(0xFE40L, "T2");
        m.put(0xFE42L, "T3");
        m.put(0xFE44L, "T4");
        m.put(0xFF40L, "T2CON");
        m.put(0xFF42L, "T3CON");
        m.put(0xFF44L, "T4CON");

        // === ADC & UART ===
        m.put(0xFEA0L, "ADCCON");
        m.put(0xFEA2L, "ADDAT");
        m.put(0xFEB0L, "S0CON");
        m.put(0xFEB2L, "S0BUF");
        m.put(0xFEB4L, "S0BG");

        return m;
    }
}