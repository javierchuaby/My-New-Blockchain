# My Blockchain Implementation 🔗

A simple yet functional blockchain built from scratch in **Java**!
This project was born out of curiosity about how cryptocurrencies like Bitcoin work under the hood. Instead of just reading about blockchain technology, I decided to roll up my sleeves and build one myself.


## 📑 Table of Contents

* [Introduction](#introduction)
* [Features](#features)
* [How It Works](#how-it-works)
* [Project Structure](#project-structure)
* [Installation & Running](#installation--running)
* [Configuration](#configuration)
* [Examples](#examples)
* [What I Learned](#what-i-learned)
* [Dependencies](#dependencies)
* [Future Ideas](#future-ideas)
* [License](#license)


## 📌 Introduction

This is a **basic blockchain** that demonstrates the core concepts powering modern cryptocurrencies such as Bitcoin. It shows how blocks are linked, mined, and validated to form a tamper-resistant chain of data.


## 🚀 Features

* **Proof-of-Work Mining**: Each block is mined by finding a hash with a specific number of leading zeros.
* **Cryptographic Security**: Uses SHA-256 hashing to secure data integrity.
* **Chain Validation**: Automatically checks for tampering.
* **JSON Output**: Pretty-prints the blockchain using Gson for readability.


## 🔧 How It Works

Each block contains:

* **Data**: A simple message (e.g., `"Hi I'm the first block"`)
* **Timestamp**: When the block was created
* **Previous Hash**: Reference to the previous block
* **Hash**: Unique fingerprint of the block
* **Nonce**: Number adjusted during mining to find a valid hash

Mining keeps incrementing the nonce until the block hash starts with enough zeros, defined by the **difficulty** setting.


## 📂 Project Structure

```
├── Block.java          # Core block implementation
├── MyChain.java        # Main blockchain logic & demo
└── utils/
    └── StringUtil.java # SHA-256 hashing utility
```

## ⚙️ Installation & Running

### Prerequisites

* Java 8 or later
* [Gson 2.6.2+](https://github.com/google/gson) in your classpath

### Compile

```bash
javac -cp ".:gson-2.6.2.jar" *.java utils/*.java
```

### Run

```bash
java -cp ".:gson-2.6.2.jar" MyChain
```

You’ll see output like:

```
Trying to Mine block 1...
Block Mined!!! : 000000a1b2c3d4e5f6...
Trying to Mine block 2...
Block Mined!!! : 000000f6e5d4c3b2a1...
```

## ⚙️ Configuration

You can tweak the **mining difficulty** in `MyChain.java`:

```java
public static int difficulty = 6;
```

* Lower numbers → faster mining
* Higher numbers → much slower (exponentially)


## 🖥️ Examples

* **Difficulty = 3** → Blocks mine quickly
* **Difficulty = 8** → Blocks take a long time to mine

This lets you see how proof-of-work affects performance.


## 📚 What I Learned

Through this project, I gained insights into:

* How cryptographic hashing creates tamper-evident structures
* Why proof-of-work is secure but energy-intensive
* The elegance of chaining blocks with hashes
* How small, simple components build powerful systems


## 📦 Dependencies

* **Java 8+**
* **Gson 2.6.2+** (for JSON output)


## 🌱 Future Ideas

* Add **digital signatures** for transactions
* Implement **network communication** between nodes
* Build a **simple wallet system**
* Experiment with **different consensus algorithms** (e.g., Proof-of-Stake)
* Create a **web interface** to visualize the chain


## ⚠️ Disclaimer

This project is for **learning purposes only**.
Do not use it as the basis of a real cryptocurrency.
