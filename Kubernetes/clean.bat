kubectl delete -f mongo-pv.yaml

kubectl delete -f mongo-deployment.yaml
kubectl delete -f mongo-service.yaml
kubectl delete -f app-deployment.yaml
kubectl delete -f app-service.yaml
kubectl delete -f mongo-pvc.yaml


REM To check for objects in k8s

kubectl get all