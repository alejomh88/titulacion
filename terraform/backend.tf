terraform {
  backend "s3" {
    bucket = "edugpt-app"
    region = "us-east-1"
    key = "eks/terraform.tfstate"
  }
}
