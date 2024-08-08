# CNSim User Guide
___

## 1. Set Up the Environment

CNSim can be run on any operating system that supports Java (i.e. Windows, Mac, Ubuntu, Linux, Unix).
Click the link for more information on [Java Installation and Usage](https://www.java.com/en/download/help/download_options.html).

[Java 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) is recommended for CNSim.
With newer JDK versions installed, it is advisable to use a lower language level.

For example, InteliJ IDEA can be configured to a lower language as follows:

File -> Project Structure -> Project Settings -> Project -> Language Level, as follows:

![Intelij IDEA Lower Language Config](/docs/ImagesDocs/ProjectStructureScreenshot.png)

___
## 2. Download CNSim
Download the CNSim repository as a zip file from GitHub and unzip or clone the repository.

Command for clone using an SSH Key: <br>
`$ git clone git@github.com:cmg-york/cnsim.git `

### 2-a. Directory structure
The structure of the unzipped or cloned directory is as follows:

```
cnsim
+-- docs
+-- refs
+-- resources
+-- src
|   +-- cmg
|       +-- cnsim
|       |   +-- bitcoin
|       |   |   +-- test
|       |   +-- engine
|       |       +-- commandline
|       |       +-- event
|       |       +-- message
|       |       +-- network
|       |       +-- node
|       |       +-- test
|       |       +-- transaction
|       |   +-- tangle
|       |       +-- test
+-- tests
+-- tools
|   +-- visualization
:
```

#### Directory Descriptions

| Directory       | Description                                                                                                   | 
|:----------------|:--------------------------------------------------------------------------------------------------------------| 
| *docs*          | Proposed folder addition to contain Documents about CNSim (e.g., Usage, Getting Started, Conceptual Document) | 
| *refs*          | Reference documents, table of units                                                                           | 
| *resources*     | Configuration files, scripts, data files                                                                      | 
| *src/cmg/cnsim* | Source codes, consisting of 3 packages: Bitcoin, Engine, Tangle                                               | 
| *tests*         | Testing                                                                                                       | 
| *tools*         | Various tools (i.e. visualization)                                                                            |

___
## 3. Run CNSim

To get started with CNSim (Bitcoin):

      1. Locate the Bitcoin package in `src/cmg/cnsim` directory.
---
      2. Locate the BitcoinMainDriver Class in the Bitcoin package.
---
      3. Run MainDriver.
---
      4. View the produced usage message in the console. This message (also available below for ease of reference) summarizes available options and expected arguments, along with a brief description of each option.
---
      5. Pass in command line arguments by adjusting the IDE run configuration by following the steps:

        5.1. In InteliJ IDEA, click on Run -> Edit Configurations, as follows: 
![CLI Arguments](/docs/ImagesDocs/EditConfigScreenshot.png)

        5.2. In the program arguments field, type in the desired command line arguments:

        Usage: cnsim [options]

        Options:

        | Option                       | Description                                 | 
        |:-----------------------------|:--------------------------------------------| 
        | -c, --config <file>          | Configuration file path (required)          | 
        | --wl <file>                  | Workload file path                          | 
        | --net <file>                 | Network file path                           | 
        | --node <file>                | Node file path                              | 
        | --out <directory>            | Output directory path                       | 
        | --ws, --workload-seed <long> | Workload seed                               | 
        | --ns, --node-seed <list>     | Node seed list (format: {long,long,...})    | 
        | --st, --switch-times <list>  | Switch times list (format: {long,long,...}) |  
        | --es, --net-seed <long>      | Network seed                                | 
        | -h, --help                   | Print this help message     

        Detailed descriptions on the addition and modification of the CLIs is available in the How-To Document in the `docs` directory.

            5.2.1. For the quickest setup, type in the following argument: `- c resources/config.txt` which will set up the configuration based solely on the config.txt file.

---
      6. Run MainDriver 
      
         6.1. Sample of a produced output:
![Output](/docs/ImagesDocs/SampleOutputScreenshot.png)

    7. Locate the simulation logs located in the appeared `logs` directory. The following simulation logs should be displayed:

(add brief description of each)
- Blocklog
- Config
- EventLog
- Input
- NetLog
- Nodes
- StructureLog