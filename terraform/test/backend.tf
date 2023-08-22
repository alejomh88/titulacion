terraform {
  backend "s3" {
    bucket = "edugpt-app1"
    region = "us-east-1"
    key = "eks/terraform.tfstate"
  }
}
