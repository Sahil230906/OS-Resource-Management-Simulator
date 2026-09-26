# OS Resource Management & Algorithm Simulator

## About the Project
The OS Resource Management & Algorithm Simulator is a JavaFX desktop application built for the OSDS course. It simulates and visualizes five core Operating System concepts — CPU scheduling, memory allocation, page replacement, disk scheduling, and deadlock detection — letting users run real algorithms on their own input and see the results play out step by step, instead of relying on static textbook diagrams. Every visualization in the app (Gantt charts, memory maps, page-frame timelines, disk seek graphs, safe-sequence traces) is generated dynamically from the actual output of the algorithm running on whatever data the user enters — nothing is pre-drawn or hardcoded.

## Problem Statement
Operating System concepts like CPU scheduling, memory allocation, page replacement, disk scheduling, and deadlock detection are usually taught through static textbook examples and hand-drawn diagrams. Students memorize the steps for one fixed example but rarely get to experiment with their own data or directly compare how different algorithms in the same category perform against each other. This makes it hard to build real intuition for the trade-offs each algorithm makes. This project addresses that gap by making every algorithm interactive, visual, and directly comparable.

## Objectives
- Build an interactive simulator covering all five core OS resource-management domains
- Let users input custom data — not just textbook examples — and see real algorithm output
- Visualize each algorithm's execution dynamically, with nothing hardcoded or pre-drawn
- Allow direct performance comparison between multiple algorithms within the same domain
- Verify every algorithm's correctness against hand-traced textbook data before trusting the output

## Technology Stack
- **Language:** Java 17
- **UI Framework:** JavaFX 21 (FXML for layout, paired Controller classes for logic)
- **Build Tool:** Maven
- **Testing:** JUnit 5, with the Surefire plugin for automated test runs
- **Architecture:** Strict layered MVC — FXML (View) → Controllers → Algorithm Layer → Result Models → Visualization Layer

## System Architecture
The application follows a strict layered design, with each layer having exactly one responsibility:

- **Algorithm Layer** (`com.ossim.algorithms.*`) — pure Java logic with zero JavaFX dependency. Each algorithm implements a shared interface within its domain (`CpuSchedulingAlgorithm`, `MemoryAllocationAlgorithm`, `PageReplacementAlgorithm`, `DiskSchedulingAlgorithm`), so every algorithm in a domain is interchangeable and can be swapped in and out — this is what makes the Comparison module possible, since it just runs every implementation of the interface against the same input.
- **Result Models** (`com.ossim.models.*`) — each algorithm run produces a structured result object (e.g. `CpuSchedulingResult`, `PageReplacementResult`) holding both summary statistics (averages, totals, hit/fault counts) and a full per-step breakdown of what happened at each point in the simulation.
- **Visualization Layer** (`com.ossim.visualization.*`) — pure rendering functions that take a result object and draw it. Since these functions only ever read from the result data, the visualizations are guaranteed to reflect exactly what the algorithm produced, with reveal animations added purely for presentation.
- **Controllers** (`com.ossim.controllers.*`) — wire up the FXML UI, validate user input (rejecting negative values, zero burst times, out-of-range disk tracks, etc.), invoke the algorithm layer, and hand the results to the visualizers and result tables.

## Module 1: CPU Scheduling
Simulates four CPU scheduling algorithms on a set of processes, each defined by an arrival time, burst time, and priority:

- **First Come First Serve (FCFS):** executes processes strictly in arrival order. Simple to implement, but a long process can delay every process behind it (the "convoy effect"). Time complexity O(n log n) for sorting by arrival time. Common in batch systems and any simple first-come-first-served queue.
- **Shortest Job First (SJF):** always picks the process with the smallest burst time among those that have arrived. Minimizes average waiting time but can starve longer processes indefinitely. O(n log n). Rarely used as-is in real systems since exact burst time isn't known in advance — real schedulers approximate it from past behavior.
- **Priority Scheduling:** the process with the highest priority (lowest number) among arrived processes runs next. Low-priority processes may starve. O(n log n). Used in real-time and embedded systems where certain tasks — like handling a hardware interrupt — must always preempt lower-priority work.
- **Round Robin:** each process gets a fixed time quantum; if it doesn't finish, it goes to the back of the ready queue. Fair and responsive, but overhead increases if the quantum is too small. O(n) per full cycle through the queue. The basis of time-sharing systems — modern Linux and Windows schedulers use variants of this for interactive processes.

**Example result** (P1: arrival 0/burst 5, P2: arrival 1/burst 3, P3: arrival 2/burst 7, run with FCFS): Average Waiting Time = 3.33 ms, Average Turnaround Time = 8.33 ms, Average Response Time = 3.33 ms.

## Module 2: Memory Management
Simulates fixed-partition memory allocation, fitting a list of processes (each with a memory size requirement) into a list of fixed-size partitions:

- **First Fit:** scans partitions in order and allocates to the first one large enough. Fast — O(n) per allocation — but can leave awkward unused gaps early in memory. Favored where allocation speed matters more than minimizing waste, such as simple embedded allocators.
- **Best Fit:** scans all partitions and picks the smallest one that still fits, minimizing leftover space in that specific block. Tends to create many small, hard-to-use fragments over time. O(n) per allocation. Suited to memory-constrained systems.
- **Worst Fit:** scans all partitions and picks the largest available one, leaving the biggest possible leftover for future processes. Wastes more memory per allocation but keeps large blocks in reserve longer. O(n) per allocation. Rarely used in practice, but useful for understanding fragmentation trade-offs.

**Example result** (Partitions: 100, 500, 200, 300, 600 KB; Processes: 212, 417, 112, 426 KB):
| Algorithm | Allocated | Failed | Total Internal Fragmentation |
|---|---|---|---|
| First Fit | 3 | 1 | 559 KB |
| Best Fit | 4 | 0 | 433 KB |
| Worst Fit | 3 | 1 | 659 KB |

Best Fit is the only one that successfully allocates all four processes with this data, at the cost of more scanning per allocation.

## Module 3: Page Replacement
Simulates page replacement in a fixed number of memory frames, given a sequence of page references:

- **FIFO:** evicts whichever page has been in memory longest, regardless of how recently it was used. Simple — O(1) with a queue — but can evict a page about to be reused (Belady's anomaly can even make more frames perform worse). Rarely used alone in real systems.
- **LRU (Least Recently Used):** evicts the page that hasn't been accessed for the longest time, exploiting temporal locality. True LRU is O(1) with the right data structures (hash map plus doubly linked list), though naive implementations are O(n). Real systems, including Linux, approximate LRU in hardware via clock/second-chance algorithms since perfect LRU is expensive to maintain.
- **Optimal:** evicts whichever page won't be needed for the longest time in the future. Provably produces the fewest possible faults, but requires knowing the entire future reference string in advance — impossible in a running system. Used purely as a theoretical benchmark to measure how close FIFO or LRU come to the best possible result.

**Example result** (reference string 7, 0, 1, 2, 0, 3, 0, 4 with 3 frames):
| Algorithm | Hits | Faults |
|---|---|---|
| FIFO | 1 | 7 |
| LRU | 2 | 6 |
| Optimal | 2 | 6 |

LRU already matches the Optimal fault count on this reference string, showing it's a strong practical approximation.

## Module 4: Disk Scheduling
Simulates disk head movement across a set of track requests, starting from a given head position:

- **FCFS:** services requests strictly in arrival order. Simple and fair, but can cause large, wasteful head movements if requests are scattered. Effectively O(n).
- **SSTF (Shortest Seek Time First):** always services whichever remaining request is closest to the current head position. Minimizes movement at each step but can starve requests far from the current cluster of activity. O(n²) across n requests.
- **SCAN (Elevator Algorithm):** the head sweeps fully to one end of the disk, servicing requests along the way, then reverses and sweeps to the other end. Avoids starvation at the cost of some unnecessary movement to reach each boundary. O(n log n) for sorting requests. This is essentially how real HDD firmware schedules seeks.
- **C-SCAN:** like SCAN, but never reverses direction — after reaching one end, it jumps straight back to the opposite end and continues the same way. Gives more uniform wait times across all requests, at the cost of the long circular jump. Preferred in systems needing predictable response times, such as database servers.

**Example result** (head starts at 50, disk size 199, requests: 98, 183, 37, 122, 14, 124, 65, 67):
| Algorithm | Total Head Movement | Average Movement |
|---|---|---|
| FCFS | 643 tracks | 80.4 tracks |
| SSTF | 205 tracks | 25.6 tracks |
| SCAN | 249 tracks | — |
| C-SCAN | 383 tracks | — |

SSTF cuts total head movement by more than two-thirds compared to FCFS on this scattered request set.

## Module 5: Deadlock Detection
Implements the **Banker's Algorithm**, a deadlock-avoidance algorithm that determines whether a system is in a "safe state" — i.e., whether there exists some order in which all processes can finish without deadlocking — given each process's current allocation, maximum future need, and the resources currently available. It works by repeatedly checking whether any process's remaining need can be satisfied by the currently available resources; if so, that process is assumed to finish and release its resources back into the pool, and the check repeats until either every process finishes (safe) or no process can proceed (unsafe).

**Example result** (5 processes, 3 resource types, Available = [3, 3, 2]): the system is found **Safe**, with safe execution sequence **P1 → P3 → P4 → P0 → P2**.

## Algorithm Comparison Module
A dedicated module that runs every algorithm within a chosen domain (CPU, Memory, Page Replacement, or Disk) on the same input side by side, and highlights the best performer with the reasoning behind the result — for example, showing which CPU scheduling algorithm produced the lowest average waiting time on the entered process list.

## Key Features
- **Run Everything (Demo Mode):** a single click auto-loads example data and runs every module in sequence, built for quick, reliable demonstration during evaluation.
- **Fully dynamic visualizations:** every chart, map, and trace is generated from real algorithm output at runtime — nothing is a static image or pre-scripted animation.
- **Input validation:** every module rejects invalid input (negative values, zero burst time, out-of-range disk tracks, allocation exceeding max, etc.) with clear error messages instead of failing silently.
- **Algorithm explanations:** every algorithm includes a written explanation of how it works, its time complexity, and real-world use cases, directly in the UI.
- **Consistent, non-repetitive dashboard:** a redesigned home screen with a hero section and distinct icon/description per module, so navigation and content don't feel duplicated.

## Testing & Verification
The project includes **25 JUnit 5 test cases** spanning all five algorithm modules (`CpuSchedulingTest`, `MemoryManagementTest`, `PageReplacementTest`, `DiskSchedulingTest`, `BankersAlgorithmTest`). Every test's expected output was hand-traced from standard OS textbook examples before being written into code — the same examples shown above — covering both normal cases and edge cases: a single process, zero internal fragmentation, a process too large to allocate, an empty-frame page reference pattern, duplicate disk requests, and a deliberately unsafe resource state with no valid safe sequence. All 25 tests currently pass, confirming that the algorithm layer is correct independent of the UI.

## Conclusion
Building this simulator reinforced the core OS scheduling and resource-management algorithms taught in the OSDS course — not just how each algorithm works, but the concrete trade-offs between them, since running them side by side on the same data makes the differences in fault counts, head movement, and waiting time immediately visible instead of abstract. It also applied real software engineering practices to a non-trivial desktop application: a strict layered architecture, separation of concerns between algorithm logic and UI, and a test suite that validates correctness independently of what's shown on screen.
