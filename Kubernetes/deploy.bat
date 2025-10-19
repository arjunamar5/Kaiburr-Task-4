kubectl apply -f mongo-pv.yaml
kubectl apply -f mongo-pvc.yaml
kubectl apply -f mongo-deployment.yaml
kubectl apply -f mongo-service.yaml
kubectl apply -f app-deployment.yaml
kubectl apply -f app-service.yaml