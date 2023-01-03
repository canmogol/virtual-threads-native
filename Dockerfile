FROM graal19:0.0.1 as builder
WORKDIR /
ADD . /

RUN ["/bin/bash", "-c", "source /root/.sdkman/bin/sdkman-init.sh \
&& cd / \
&& ./mvnw clean install \
&& native-image --version \
&& native-image --enable-preview --no-fallback -H:+ReportExceptionStackTraces -jar target/VirtualThreadApplication.jar \
"]

FROM ubuntu:22.10
COPY --from=builder /VirtualThreadApplication /VirtualThreadApplication
EXPOSE 8080
CMD ["/VirtualThreadApplication"]
