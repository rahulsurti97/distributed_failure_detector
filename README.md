# ECE428 - MP2

This is our implementation of a distributed group membership system.
### How to Start
- Clone the repo using `git clone`
- `cd` into the scripts folder
- You can run the server by typing `./start_server.sh`
- On a separate set of 10 machines, do the following:
- Repeat the steps for MP1 for the `grep` system. You need another 10 VMs open. Run 9 servers by running `server_mp1.sh`
- On the "client" for the VM, run `client_mp1.sh`
- This should let you grep the files. An example command will be something like `grep -a "true"`

### Description
This program will create a network of nodes and handle failure detectors. The failure detection will be spread across all nodes in the network quickly and everyone _should_ be updated quickly on the status of the entire network using the gossiping algorithm and membership lists.

We tested the program using our MP1 code. Our MP1 grep system uses TCP (piazza post said that was OK), so we are always able to grep the files that we are looking for. There were slight modifications done for MP1's code, but nothing major to the point the entire MP was changed. (about 5-7 lines of code were changed)
