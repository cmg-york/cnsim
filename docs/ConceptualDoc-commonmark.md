

# CNSim – Event-Based Simulator for Consensus Networks

## Draft Conceptual Document:

### CNSim Overview:

CNSim is a consensus network simulation system designed as an
event-based simulator to simulate the performance of different types of
blockchains (e.g. Bitcoin, Tangle).

The simulator represents a framework which individuals can use to
simulate any consensus protocol by using the key components of the
Engine, which is the key idea of the CNSim. One of the benefits of
developing such a simulator is its efficiency and flexibility, which
allows to study various consensus protocols (e.g. Bitcoin, Tangle,
Ethereum, etc.), allowing individuals to download the Engine and create
their own consensus network simulator and implementing their own
protocol. The existing framework allows to simulate large numbers of
network events and node behaviors for different consensus protocols
without having to implement everything from scratch.

This document focuses on outlining the architecture and main components
of the existing simulator as well as record suggestions for improvement.
The document will be edited/expanded over time once the team becomes
more familiar with CNSim, improves current core functions and implements
new features.

### Scope:

The simulator does not focus on hops of the messages from one node to
another as it would be time-consuming and inefficient to simulate all
propagation events for every node. Therefore, we assume that every node
is connected with all other nodes conceptually.

### Architecture & Main Components:

The simulator contains multiple packages, each being responsible for a
particular function of the program: <br> \#### Bitcoin: contains a
number of classes designed to simulate the Bitcoin consensus network:

<table>
<colgroup>
<col style="width: 4%" />
<col style="width: 95%" />
</colgroup>
<thead>
<tr class="header">
<th><strong>Class</strong></th>
<th><strong>Description</strong></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><strong>BitcoinMainDriver</strong></td>
<td>Entry point of the CNSim for Bitcoin-based simulations. <br> <br/>
The class is responsible for reading settings from a configuration file,
which sets the simulation parameters (e.g., number of nodes, random
seeds, number of transactions, etc.). <br> The driver allows for
creation of a Sampler, which is either “file-based” (loads from files or
generates standard data) or “standard sampler” (relies on standard data)
depending on configuration. <br> In addition, the driver: </td>
</tr>
<tr class="even">
<td><strong>BitcoinNode (subclass of Node class)</strong></td>
<td></td>
</tr>
<tr class="odd">
<td><strong>Bitcoin Reporter (subclass of Reporter class)</strong></td>
<td>Responsible for adding logs regarding Bitcoin’s blockhain and block
information, producing BlockLog (to track the state of the Bitcoin
blocks such as block content, difficulty, cycles, etc.) and StructureLog
(to track the state of the Bitcoin blockchain structure, including
sequences and orphans).</td>
</tr>
<tr class="even">
<td><strong>Block (subclass of TransactionGroup)</strong></td>
<td>Represents a block in Bitcoin blockchain &amp; extends Transaction
Group, a list to contain transactions. Each block contains a list of
grouped transactions and other data, including block’s identity,
position, validation process and current status. This information allows
to view and track block’s history, and maintaining integrity and order
of the Bitcoin’s blockchain.</td>
</tr>
<tr class="odd">
<td><strong>Blockchain</strong></td>
<td>Represents how blocks, orphans and tips (latest blocks) are managed
in a Bitcoin blockchain &amp; implements IStructure. Blocks are
connected in a blockchain starting from the genesis block to the head of
the chain. (add more)</td>
</tr>
<tr class="even">
<td><strong>Block Height Comparator</strong></td>
<td>Compares blocks based on their position in the blockchain
(i.e. height) and then further sorts them by IDs (if heights are the
same), which allows the ability to perform operations in the right
order.</td>
</tr>
<tr class="odd">
<td><strong>Honest Node Behaviour</strong></td>
<td>Specifies standard “honest” node behaviour, engaging in receiving,
adding valid transactions to the node’s pool, propagating to other nodes
and block validating activities as per normal rules.</td>
</tr>
<tr class="even">
<td><strong>Malicious Node Behaviour</strong></td>
<td>Specifies “malicious” node behaviour, attempting to shadow “honest”
behaviour by receiving and propagating transactions but may also attack.
If an attack is in progress, a malicious node will attempt to add new
blocks to a hidden chain, while validating and extending the chain, and
deciding when to reveal the hidden chain (if the hidden chain is longer
than the public chain or because of the public chain reaching maximum
lenght). The goal of such behaviour is to cause disruptions to the
Bitcoin blockchain’s integrity and replace the valid public chain with a
hidden “malicious” one.</td>
</tr>
</tbody>
</table>

#### Engine:

The Engine includes main components that allow event management,
specifies network structure, defines the node and its functions, as well
as specifies the nodeset in a network. Engine represents the main idea
behind the CNSim, allowing developers to download the engine and develop
their own simulator and implement their own protocol(this is what is
being done with Bitcoin and Tangle). Engine is designed to work
independently of protocols.

The Node implements the logic and the rules of the Network with the Node
being specialized (e.g. Bitcoin, Tangle or other created protocol) and
responds to an Event in a defined behaviour.

The Engine package contains 5 subpackages:

<table>
<colgroup>
<col style="width: 1%" />
<col style="width: 98%" />
</colgroup>
<thead>
<tr class="header">
<th><strong>Subpackage</strong></th>
<th><strong>Description</strong></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><strong>Event</strong></td>
<td>Superclass Event and 4 of its subclasses – event types: </td>
</tr>
<tr class="even">
<td><strong>Message</strong></td>
<td>Responsible for protocol coordination, allows for message exchange
between different nodes of the network.</td>
</tr>
<tr class="odd">
<td><strong>Node</strong></td>
<td>Contains basic structure of the node in a blockchain consensus
network, specifies its basic attributes and functionalities for a node
in a network (e.g. event handling and management of transactions); is
designed as a base class for specialized nodes (e.g. Bitcoin, Tangle)
Nodes, each with a unique ID, participate in simulations that model the
blockchain consensus network</td>
</tr>
<tr class="even">
<td><strong>Transaction</strong></td>
<td>Represents a transaction in a consensus network. This subpackage
contains the following important classes of CNSim: <br> <br/> </td>
</tr>
</tbody>
</table>

<br/>
<li>

Simulation Class: Engine also contains the Simulation class, one of the
key classes of CNSim. (TO DO: add simulation description)

### Tangle:

The team will add to this part once we familiarize ourselves with this
portion of the code.
