# Dockerfile
FROM hseeberger/scala-sbt:17.0.2_1.6.2_3.1.1
#FROM hseeberger/scala-sbt:8u222_1.3.5_2.13.1


# Setzen Sie die Umgebungsvariable DISPLAY
ENV DISPLAY=host.docker.internal:0.0

# Installieren Sie die notwendigen Bibliotheken für die GUI
RUN apt-get update && \
    apt-get install -y --no-install-recommends openjfx && \
    rm -rf /var/lib/apt/lists/* && \
    apt-get install -y  libxrender1 libxtst6 libxi6

# Setzen Sie das Arbeitsverzeichnis
WORKDIR /gui

# Kopieren Sie das Projektverzeichnis in den Docker-Container
ADD . /gui

CMD sbt run