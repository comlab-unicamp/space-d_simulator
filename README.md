<p align="center"> 
<img src=https://github.com/alaelson/simulator/blob/master/misc/logo_space-d.png> <br>
<h1 align="center">A simulation tool for space-division multiplexing elastic optical networks<h1>
</p> 
  
About Space-D
--------------
This software is a custom discrete event-driven simulator for Space-division Multiplexed Elastic Optical Networks (SDM-EON) with support to uncoupled channels that are represented by single-mode multi-core fibers as the links of the network. 

The dynamics of the system is represented by the event chaining. The events are associated with the snapshot system actions that indicate state transitions and can trigger other events that will be executed in the future. 

The events are connection requests to connect source-destination pairs of nodes in the network. The network is managed by the Control Plane, which checks if there are enough resources to accomodate all requirements before to install each connection request in the network. 


  
