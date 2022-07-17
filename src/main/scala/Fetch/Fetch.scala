package Fetch

import chisel3._
import chisel3.util.experimental.loadMemoryFromFile

class Fetch_IO extends Bundle
{
    // Input pins
    val nPC_in: UInt = Input(UInt(32.W))
    val nPC_en: Bool = Input(Bool())

    // Output pins
    val PC_out: UInt = Output(UInt(32.W))
    val inst_out: UInt = Output(UInt(32.W))
}
class Fetch extends Module
{
    // Initializing IO pins
    val io: Fetch_IO = IO(new Fetch_IO())

    // Input wires
    val nPC_in: UInt = dontTouch(WireInit(io.nPC_in))
    val nPC_en: Bool = dontTouch(WireInit(io.nPC_en))

    // Program counter
    val PC: UInt = dontTouch(RegInit(0.U(32.W)))

    // Instruction memory
    val inst_mem: Mem[UInt] = Mem(1024, UInt(32.W))

    // Loading instructions from memory
    loadMemoryFromFile(inst_mem, "assembly/hex.txt")

    // Output wires
    val PC_out: UInt = dontTouch(WireInit(PC))
    val inst_out: UInt = dontTouch(WireInit(inst_mem.read(PC(25, 2))))

    // Wiring to output pins
    Array(        // Output pins
        io.PC_out, io.inst_out, PC
    ) zip Array(  // Corresponding wires
        PC_out,    inst_out,    Mux(nPC_en, nPC_in, PC + 4.U)
    ) foreach
    {
        x => x._1 := x._2
    }
}
