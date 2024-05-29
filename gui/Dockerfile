FROM hseeberger/scala-sbt:17.0.2_1.6.2_3.1.1

# Install X Window System libraries
RUN apt-get update && apt-get install -y libxext6 libxrender1 libxtst6

# Set the working directory
WORKDIR /app

# Copy the project, excluding specified directories and files
ADD . /app
RUN find . -name target -type d -exec rm -rf {} + && \
    rm -rf .idea .bsp field.json field.xml

RUN sbt compile

# Run the GuiServer
CMD sbt "gui/runMain GuiServer" && tail -f /dev/null