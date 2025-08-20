FROM openjdk:21-jdk-bookworm

# Install xargs (from findutils) and clean up apt cache
RUN apt-get update \
    && apt-get install -y --no-install-recommends findutils \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app
