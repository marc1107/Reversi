# only works on Windows
# https://sourceforge.net/projects/xming/ --> install this for X Server (needed to run)

# docker build -t reversi-image .
# docker run -ti reversi-image

# hseeberger container with Display Variable
FROM hseeberger/scala-sbt:17.0.2_1.6.2_3.1.1
RUN apt-get update && apt-get install -y \
    libxrender1 libxtst6 libgl1-mesa-glx libgtk-3-0 \
    libcanberra-gtk-module libcanberra-gtk3-module

ENV DISPLAY=host.docker.internal:0

# work directory and local file combine
WORKDIR /Reversi
ADD . /Reversi

# run sbt
CMD sbt run