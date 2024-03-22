# testing-sql-queries

This code base supports the [Principles of automated testing for data warehouses](https://leecampbell.com/2024/03/21/principles-of-automated-testing-for-data-warehouse-queries/).

The code base demonstrates how to apply the principles of segregation of responsibilities, isolation of dependencies, specification-based testing, and automated testing to querying distributed data sets within a data warehouse.
These techniques enable data analysts to tackle the challenges of distributed data ownership, query accuracy, and resilience to changing models and requirements.

## Running the sample

You will need Docker installed and running for the code base to run locally.

For linux and MacOS systems run the shell script

```bash
./run.sh
```

For Windows systems run the Powershell script

```Powershell
.\run.ps1
```

The script will start up a Docker Compose stack with a PostgreSQL database.
It will then run three docker instances that run SQL migrations to create the three schemas used in the test.
Finally, it will run the tests.

As the process is finishing, you should see some output that tells you the tests have successfully run.

```bash
| ---------------------------------------------------------------
| |  Results: SUCCESS (6 tests, 6 passed, 0 failed, 0 skipped)  |
| ---------------------------------------------------------------
```

Once the tests have completed you will be prompted to exit the Docker Compose process.

```bash
|  ---------------------------------------------------------------
|     Tests have completed.
|
|     Press ctrl+c to exit the docker process
|  ---------------------------------------------------------------
```
