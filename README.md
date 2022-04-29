# com.marcsys.benchmark
Benchmark repository to allow integrating with HDR Histogram in a easy way.

## Why I write this lib?
I worked with HDRHistogram few times, for every time I was needed to read the documentation to remember the settings and the commands to convert .perf file.

For this reason I decide to create this lib which consolidates the main settings normally used and the instructions to carry out the conversation.

## How to configure the project
- If you **fork/clone** this project, you need to add the dependency on the pom.xml.
  ```xml
  <dependencies>
    <dependency>
        <groupId>com.marcsys</groupId>
        <artifactId>com.marcsys.benchmark</artifactId>
        <version>0.0.1-SNAPSHOT</version>
    </dependency>
  </dependencies>
  ```
- Configure the **_application.properties / applications.yml_** as bellow. 
  ```yml
  #default value is false
   benchmark:
    enabled: true 
  ```
- Two others configurations are available:
  ```yml
  benchmark:
    folder: /var/log/app_name # Folder where the .perf file will be created (default UNIX-/var/log/app_name Windows-C:\app_name ) 
    border:
      mean:
        count: 100 # Number of samples (default 100)
  ```

## Default values
- In order to prevent errors default values are expected for the above configurations.
```yml
  benchmark:
    enable: false
    folder: log/app_name # Folder where the .perf file will be created (default UNIX-/var/log/app_name Windows-C:\app_name ) 
    border:
      mean:
        count: 100 # Number of samples (default 100)
  ```

## How to use
With the lib configured properly, it's need to include on top of the method you will monitoring the annotation **@Benchmark**;
i.e.

  ```java
import com.marcsys.monitoring.benchmark.annotations.Benchmark;

@Benchmark
public void execute(){
  // Method impletation here
}
  ```

## Checking the results
A **_.perf_** file should be generated as above with the pattern name
*void_packageName.ClassName.methodName.perf*

The content to the generated file is not human-readable.

```txt
#[Histogram log format version 1.3]
#[StartTime: 1633286203.972 (seconds since epoch), Sun Oct 03 19:36:43 BST 2021]
"StartTimestamp","Interval_Length","Interval_Max","Interval_Compressed_Histogram"
0.000,0.617,26.083,HISTFAAAADR42pNpmSzMwMAgygABTFCaEUz8///f/gNE4LYcUyszkzaTJVMxkzUTNxMTO1MuEwD7aAiy
0.621,0.643,17.957,HISTFAAAADF42pNpmSzMwMAgxAABTFCaEUz8///f/gNEYLcCkybTYUYmaSZLlmgWSSZuJgDfkAg4
1.265,0.610,17.957,HISTFAAAADR42pNpmSzMwMAgxgABTFCaEUz8///f/gNE4KQY01wOpr2MTOZM7kCczLSRkYmbBQAO3gm4
1.875,0.608,17.039,HISTFAAAADN42pNpmSzMwMAgwgABTFCaEUz8///f/gNEYKs402cOJlsmS6ZkJlkmJkmmzYwsAPfkCSk=
```

## Converting the File
To convert the file you need to use **org.HdrHistogram.HistogramLogProcessor** class.
org.HdrHistogram.HistogramLogProcessor is provided by the project [HdrHistogram](https://github.com/HdrHistogram/HdrHistogram).

On this repository I included *lib/HdrHistogram.jar (version 2.1.13)*, that allow to execute the bellow steps.

```shell
#On the lib folder
java -cp HdrHistogram.jar org.HdrHistogram.HistogramLogProcessor -i absolute_path/myfile.perf > outfile.hgrm
```
The above command you produce a file with a content like below. 
```csv
#[Overall percentile distribution between 0.000 and <Infinite> seconds (relative to StartTime)]
#[StartTime: 1633286203.972 (seconds since epoch), Sun Oct 03 19:36:43 BST 2021]
       Value     Percentile TotalCount 1/(1-Percentile)

        0.06 0.000000000000          1           1.00
        1.97 0.100000000000        108           1.11
        4.92 0.200000000000        215           1.25

      ...
      
       20.05 0.998437500000       1069         640.00
       20.05 0.998632812500       1069         731.43
       20.05 0.998828125000       1069         853.33
       20.05 0.999023437500       1069        1024.00
       26.08 0.999121093750       1070        1137.78
       26.08 1.000000000000       1070
#[Mean    =        10.33, StdDeviation   =         5.89]
#[Max     =        26.08, Total count    =         1070]
#[Buckets =           18, SubBuckets     =          256]
```
Use this file to plot a graph on [HdrHistogram Plotter](https://hdrhistogram.github.io/HdrHistogram/plotFiles.html)