# Week 13 — Ansible Configuration Management & Infrastructure Automation

## Overview
This document details the configuration management automation implemented using Ansible playbooks, inventory definitions, security role tasks, firewall rules, and container deployment tasks.

---

## 1. Ansible Directory Structure & Files

The Ansible project structure resides in the [`ansible/`](../ansible) directory:

```
ansible/
├── ansible.cfg                 ← Ansible engine defaults & privilege escalation configuration
├── inventory.ini               ← Staging & production target host definitions
├── playbook.yml                ← System package, Docker, and firewall provisioning playbook
└── roles/
    └── cirt_app/
        └── tasks/
            └── main.yml        ← Application deployment & healthcheck URI task
```

---

## 2. Inventory Specification ([`ansible/inventory.ini`](../ansible/inventory.ini))

```ini
[staging]
staging-server ansible_host=192.168.1.100 ansible_user=ubuntu

[production]
prod-server-01 ansible_host=192.168.1.200 ansible_user=ubuntu

[all:vars]
ansible_python_interpreter=/usr/bin/python3
cirt_app_dir=/opt/cirt
cirt_port=9090
cirt_db_port=5432
```

---

## 3. Provisioning Playbook ([`ansible/playbook.yml`](../ansible/playbook.yml))

The main playbook automates 6 key server setup steps:
1. **Package Cache Update**: Refreshes `apt` repositories.
2. **System Dependencies**: Installs `docker.io`, `docker-compose-plugin`, `curl`, `ufw`.
3. **Service Management**: Enables and starts the Docker system service.
4. **Directory Creation**: Provisioning `/opt/cirt` with `0755` ownership for deployment user `ubuntu`.
5. **Firewall Rules (UFW)**: Enables firewall and allows TCP ports `22` (SSH), `80` (HTTP), `443` (HTTPS), `9090` (CIRT Application).
6. **Role Invocation**: Triggers `cirt_app` role to copy compose config, spin up containers, and verify health endpoints.

---

## 4. Role Task Specification ([`ansible/roles/cirt_app/tasks/main.yml`](../ansible/roles/cirt_app/tasks/main.yml))

```yaml
---
- name: Copy docker-compose.yml to application directory
  copy:
    src: ../../../docker-compose.yml
    dest: "{{ cirt_app_dir }}/docker-compose.yml"
    owner: ubuntu
    group: ubuntu
    mode: '0644'

- name: Pull and launch CIRT application container stack
  command: docker compose up -d
  args:
    chdir: "{{ cirt_app_dir }}"

- name: Verify CIRT application health status
  uri:
    url: "http://localhost:{{ cirt_port }}/actuator/health"
    status_code: 200
  register: health_response
  until: health_response.status == 200
  retries: 10
  delay: 5
```

---

## 5. Execution Commands

### Testing Connection & Syntax
```bash
ansible-playbook -i ansible/inventory.ini ansible/playbook.yml --syntax-check
ansible all -i ansible/inventory.ini -m ping
```

### Running Complete Provisioning & Deployment Playbook
```bash
ansible-playbook -i ansible/inventory.ini ansible/playbook.yml
```
