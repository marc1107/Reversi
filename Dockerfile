# only works on Windows
# https://sourceforge.net/projects/xming/ --> install this for X Server (needed to run)

# docker build -t reversi-image .
# docker run -ti reversi-image


FROM hseeberger/scala-sbt:8u222_1.3.5_2.13.1
ENV DISPLAY=host.docker.internal:0.0
RUN apt-get update && \
    apt-get install -y --no-install-recommends openjfx && \
    rm -rf /var/lib/apt/lists/* && \
    apt-get install -y sbt libxrender1 libxtst6 libxi6
WORKDIR /Reversi
ADD . /Reversi
CMD sbt run