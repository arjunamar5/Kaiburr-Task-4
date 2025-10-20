kubectl delete -f mongo-pv.yaml
kubectl delete -f rbac.yaml
kubectl delete -f mongo-deployment.yaml
kubectl delete -f mongo-service.yaml
kubectl delete -f app-deployment.yaml
kubectl delete -f app-service.yaml
kubectl delete -f mongo-pvc.yaml

kubectl get all