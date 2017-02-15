Citrus Merchants Middleware system

# How to run
	1. Install Aerospike for cache.
		a. On windows:
			http://www.aerospike.com/docs/operations/install/vagrant/win
		b. On Linux:
			http://www.aerospike.com/docs/operations/install/linux
		After installation, open aerospike.conf file (at /etc/aeropsike/) and add a new namespace as below:
		namespace merchantCache {
		   replication-factor 2
		   memory-size 2G
		   default-ttl 0
		   storage-engine device {
		       file /opt/aerospike/data/merchantCache.dat
		       filesize 5G
		       data-in-memory true
		   }
		}
		
	2. Install Mongo for Main Database.
		https://www.mongodb.com/download-center?jmp=nav#community
		After installation, start mongo (mongod.bat/mongod.sh in mongo installation bin folder).
		
	3. In applicaiton.yml, change server.port to your desired port.
	
	4. Run Application.java as java application.