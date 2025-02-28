#Apache Pulsar External Connection Prototypes

##Purpose
The purpose of this set of prototypes is to provide simple external connection prototypes
for both pulsar cli and a java pulsar client. The prototypes explore first unencrypted
connections with in the Proto-01-kube, and then encrypted connections via tls with
cert-manager providing the issuers, certificates and secrets. The final 3rd prototype uses
the helm chart for deployment.

#helm install --dry-run --values ${PROTODIR}/helm/values-minikube.yaml --namespace pulsar pulsar-mini apache/pulsar



