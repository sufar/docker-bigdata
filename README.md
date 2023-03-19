
## Problems

### namenode can not start

Error message:

```
Configuring for multihomed network
Formatting namenode name directory: /hadoop/dfs/name
library initialization failed - unable to allocate file descriptor table - out of memory/run.sh: line 17:   367 Aborted                 (core dumped) $HADOOP_HOME/bin/hdfs --config $HADOOP_CONF_DIR namenode -format $CLUSTER_NAME
library initialization failed - unable to allocate file descriptor table - out of memory/run.sh: line 19:   439 Aborted                 (core dumped) $HADOOP_HOME/bin/hdfs --config $HADOOP_CONF_DIR namenode
```

Reson:

> ulimit -a

```
file descriptors 1024
```

#### linux limit

Increase max number of ulimit open file in Linux

1.Step : open the sysctl.conf and add this line fs.file-max = 65536

> $ vi /etc/sysctl.conf

add new line and

```
fs.file-max = 65536
save and exit.
```

2.Step:

> $ vi /etc/security/limits.conf

and add below the mentioned

```
* soft     nproc          65535
* hard     nproc          65535
* soft     nofile         65535
* hard     nofile         65535
```
save and exit check max open file ulimit

#### docker limit

> sudo nvim /etc/docker/daemon.json

```
{
        "default-ulimits": {
                "nofile": {
                        "Name": "nofile",
                        "Hard": 64000,
                        "Soft": 64000
                }
        }
}
```

> sudo systemctl restart docker
