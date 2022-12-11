FROM registry.access.redhat.com/ubi7/ubi-minimal

COPY VirtualThreadApplication /
EXPOSE 7070
CMD ["/VirtualThreadApplication"]