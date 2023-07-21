rs.status();

use("template-java-spring-boot-mongodb-db")

db.createUser(
    {
      user: "replica-user",
      pwd: "pa55w0rd",
      roles: [
        { role: "userAdminAnyDatabase", db: "admin" },
        { role: "readWriteAnyDatabase", db: "admin" }
      ]
    }
)