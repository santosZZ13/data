# Docker
* Docker Image 
* Docker Container 
* Docker Volume 
* Docker Network 
* Docker Compose
  * docker compose up --build
  * docker compose down
  * docker compose ps

Push image to docker hub
```bash
docker login
docker tag <image_id> <docker_hub_username>/<image_name>:<tag> Ex: docker tag data-service cucarot123/data-service:1.0
docker push <docker_hub_username>/<image_name>:<tag> Ex: docker push cucarot123/data-service:1.0
```

# K8s
+ Deployment
+ Service
  + ClusterIP
  + NodePort
  + LoadBalancer
+ Ingress
+ PersistentVolume
+ PersistentVolumeClaim
+ StatefulSet
+ Job
+ CronJob

## K8s Config
+ ConfigMap
+ Secret


1. kubectl apply -f deployment.yaml
2. kubectl apply -f service.yaml
3. kubectl get deployments
4. kubectl get pods --watch
5. kubectl logs <pod_name>
6. kubectl describe pod <pod_name>

## Minikube
```bash
minikube start
minikube dashboard
minikube stop
minikube delete
minikube service <service_name> -n <namespace> Ex: minikube service data-service -n default
```


## Google Cloud Platform
1. Install gcloud sdk
2. gcloud auth login
3. gcloud init
4. gcloud components install kubectl
5. gcloud container clusters create <cluster_name> --zone <zone> --num-nodes <num_nodes> Ex: gcloud container clusters create my-cluster --zone us-central1-a --num-nodes 3
   * Ex: gcloud container clusters create my-cluster --zone us-central1-a --num-nodes 3 --machine-type n1-standard-2

### Docker registry
* gcloud auth configure-docker
* docker tag <image_id> gcr.io/<project_id>/<image_name>:<tag> Ex: docker tag data-service gcr.io/my-project/data-service:1.0
