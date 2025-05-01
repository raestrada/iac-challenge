variable "size" {
  type    = string
  default = "small"
}

variable "name" {
  type = string
}

variable "project_id" {
  type        = string
  description = "GCP Project ID"
}

variable "region" {
  type        = string
  description = "GCP Region"
  default     = "us-central1"
}
