# Project Screenshots

Visual walkthrough of the OS Resource Management & Algorithm Simulator, covering the dashboard and all five algorithm modules plus the comparison tool. Each image below is a stacked composite of the actual running application, captured directly from the JavaFX app.

---

## 1. Dashboard — Home Screen

![Dashboard](screenshots/01-dashboard.png)

The landing screen of the simulator. A "Welcome back" hero section introduces the app, with a **Run Everything (Demo Mode)** button that auto-loads example data and results across every module in one click — useful for quickly demonstrating the whole project without manual input. Below the hero, six navigation cards (CPU Scheduling, Memory Management, Page Replacement, Disk Scheduling, Deadlock Detection, and Comparison) each carry a distinct vector icon, a short tagline, and an "OPEN →" link, so every module is visually distinguishable at a glance instead of repeating the same layout.

---

## 2. CPU Scheduling Module

![CPU Scheduling](screenshots/02-cpu-scheduling.png)

Simulates four classic CPU scheduling algorithms: **FCFS, SJF, Priority Scheduling, and Round Robin**. The user enters a set of processes (arrival time, burst time, and priority where relevant) and the module computes a Gantt chart, plus average waiting time, turnaround time, and response time. The screenshots here show FCFS and Round Robin runs on the same process set, letting the difference in scheduling strategy — and its effect on average waiting time — be seen directly. All computed values were hand-traced against the standard textbook example before being verified against the running app.

---

## 3. Memory Management Module

![Memory Management](screenshots/03-memory-management.png)

Implements the three contiguous memory allocation strategies: **First Fit, Best Fit, and Worst Fit**. Given a set of memory partitions and a set of incoming processes, the module allocates each process to a partition according to the selected strategy and reports which block it landed in, how much internal fragmentation resulted, and which processes (if any) failed to allocate. The screenshots show the classic B1–B5 partition / P1–P4 process example, with the memory map visualizing block occupancy after allocation.

---

## 4. Page Replacement Module

![Page Replacement](screenshots/04-page-replacement.png)

Simulates page replacement using **FIFO, LRU, and Optimal**, run against a configurable reference string and frame count. For each reference in the string, the module shows whether it was a hit or a fault, the frame state at that step, and which page (if any) was evicted, alongside overall hit/fault counts and ratios. The screenshots use the standard reference string `7, 0, 1, 2, 0, 3, 0, 4` with 3 frames — a widely used textbook example that also demonstrates how FIFO, LRU, and Optimal can diverge on eviction choices for the same input.

---

## 5. Disk Scheduling Module

![Disk Scheduling](screenshots/05-disk-scheduling.png)

Covers four disk head scheduling algorithms: **FCFS, SSTF, SCAN, and C-SCAN**. Given an initial head position, a set of track requests, and a disk size, the module computes the order in which requests are serviced, the total and average head movement, and a visual seek graph plotting head position over each step. The screenshots demonstrate how SSTF and SCAN reorder the same request queue very differently from FCFS's strict arrival order, directly illustrating the total-movement trade-offs discussed in the presentation content.

---

## 6. Deadlock Detection Module

![Deadlock Detection](screenshots/06-deadlock-detection.png)

Implements the **Banker's Algorithm** for deadlock avoidance. Given each process's current allocation, maximum demand, and the system's available resources, the module determines whether the system is in a safe state and, if so, produces a valid safe sequence — shown step by step with the running Work vector as each process is granted its remaining need and releases its resources back to the pool. The screenshots show the classic 5-process/3-resource example, correctly identified as **SAFE** with the safe sequence **P1 → P3 → P4 → P0 → P2**, matching the hand-traced expected result exactly.

---

## 7. Algorithm Comparison Module

![Comparison](screenshots/07-comparison.png)

Lets multiple algorithms from the same category (e.g., all four CPU scheduling algorithms, or all three page replacement algorithms) be run on identical input side by side, with their key metrics laid out in a single comparison table. This makes trade-offs concrete — for example, seeing directly how Round Robin's average waiting time compares to SJF's for the same process set — rather than having to run each algorithm separately and compare results by memory.
