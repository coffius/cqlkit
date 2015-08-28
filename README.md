This fork has been created only for one reason - to output timestamp as a date with the format `yyyy-MM-dd HH-mm-ss.SSSZ`.
It is needed to make `cqlkit` compatible with other cassandra util - `cassandra-loader` which can be used for loading csv data into cassandra.

Just add this option `-dateFormat 'yyyy-MM-dd HH-mm-ss.SSSZ'` to your `cassandra-loader` command

You can find `cassandra-loader` here: https://github.com/brianmhess/cassandra-loader

Documentation for `cqlkit` is here: https://github.com/tenmax/cqlkit
