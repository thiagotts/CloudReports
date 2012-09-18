[![Build Status](https://secure.travis-ci.org/thiagotts/CloudReports.png)](http://travis-ci.org/thiagotts/CloudReports)

![logo] (http://dl.dropbox.com/u/737234/CloudReports/cloudreportslogo.png)

CloudReports is a graphic tool that simulates distributed computing environments based
on the Cloud Computing paradigm. It uses [CloudSim][cloudsim] as its simulation
engine and provides an easy-to-use user interface, report generation features and
creation of extensions in a plugin fashion.

The application simulates an Infrastructure as a Service (IaaS) provider with an 
arbitrary number of datacenters. Each datacenter is entirely customizable. The user
can easily set the amount of computational nodes (hosts) and their resource configuration,
which includes processing capacity, amount of RAM, available bandwidth, power consumption
and scheduling algorithms.

The customers of the IaaS provider are also simulated and entirely customizable. The user
can set the number of virtual machines each customer owns, a broker responsible for allocating
these virtual machines and resource consumption algorithms. Each virtual machine has its own
configuration that consists of its hypervisor, image size, scheduling algorithms for
tasks (here known as cloudlets) and required processing capacity, RAM and bandwidth.

Additionally, CloudReports generates HTML reports of each simulation and raw data files that
can be easily imported by third-party applications such as Octave or MATLAB.

Here's a screenshot of CloudReports's GUI:

![preview] (http://dl.dropbox.com/u/737234/CloudReports/crscreenshot.png)


## Running CloudReports

You can either compile CloudReports from source or run the .jar file directly (see the
[downloads][downloadspage] page).

### Building with Maven

Some build tools such as [Ant][ant] or [Maven][maven] make compiling a Java project much easier.
Furthermore, most of the popular IDE (e.g. [NetBeans][netbeans] and [Eclipse][eclipse]) offer 
integration with these tools, which automates the whole process and saves you a lot of time.
The steps below show you how to build CloudReports with Maven and import a project to Eclipse.

First of all, make sure you have your development environment set up. If you don't,
download and install the [Java Development Kit][jdk] and [Maven][maven]. 
Notice that this project was built using JDK 6 and no tests whatsoever were performed using JDK 7.

Open a terminal in the project's folder and create the executable .jar file with all dependencies:

    mvn clean package
    mvn clean package jar:jar

Run the jar file:

    java -jar target/CloudReports.jar & exit

or

    ./bin/start.sh

If you want to import the project in Eclipse, just run:

    mvn eclipse:eclipse

### Running from binaries

Running CloudReports from binaries is very simple:

Download the binaries from the [downloads][downloadspage] page, extract it, and run the .jar file:

    java -jar CloudReports.jar & exit

## Extensions development

CloudReports supports the development of extensions that can be "plugged in" on execution time using
the Java Reflection API. These extensions can be created independently, without the need of recompiling 
CloudReports' source code. Currently, the following types of algorithms can be developed as extensions:

- Virtual machine allocation policies.
- Broker policies.
- Resource provisioners: processing elements, RAM and bandwidth.
- Tasks (cloudlets) schedulers.
- Power consumption models.
- Resource utilization models.
- Virtual machines schedulers.

For more information, visit the [wiki page about developing extensions][extensionswiki].

## Upcoming features

- Intra-datacenter networks.
- A better management of multiple environments.
- Possibility to select what kind of data you want on the reports.
- More types of extensions.
- The ones you are going to develop.

## Contributing

1. Fork it.
2. Create your feature branch (`git checkout -b my-new-feature`).
3. Commit your changes (`git commit -am 'Added some feature'`).
4. Push to the branch (`git push origin my-new-feature`).
5. Create new Pull Request.

## License

Copyright (c) 2010-2012 Thiago T. Sá

CloudReports is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

CloudReports is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

For more information, refer to the LICENSE file or see [the GNU page][gnu].

[cloudsim]: http://www.cloudbus.org/cloudsim/
[downloadspage]: https://github.com/thiagotts/CloudReports/downloads
[netbeans]:http://netbeans.org/
[eclipse]: http://www.eclipse.org/
[jdk]: http://www.oracle.com/technetwork/java/javase/downloads/index.html
[ant]:http://ant.apache.org/
[maven]: http://maven.apache.org/
[gnu]: http://www.gnu.org/licenses
[extensionswiki]: https://github.com/thiagotts/CloudReports/wiki/Developing-extensions

