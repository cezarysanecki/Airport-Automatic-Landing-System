## How to run app?

1. Run docker-compose

For Docker:
```bash
docker-compose -f docker/docker-compose.yml up -d
```

For Podman:
```bash
podman-compose -f docker/docker-compose.yml up -d
```

2. Download JavaFX™ 3D Model Importers from http://www.interactivemesh.org/models/jfx3dimporter.html

3. Place jimObjModelImporterJFX.jar in libs folder

4. Build project db-init

```bash
./db-init/gradlew -p db-init build
```

5. Run db-init application ([InitDatabaseRunner](./db-init/src/main/java/com/jakub/bone/dbinit/InitDatabaseRunner.java))

6. Run main app

```bash
./gradlew run
```
