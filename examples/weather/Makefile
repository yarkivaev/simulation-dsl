.PHONY: dev server frontend install build

dev: install
	$(MAKE) -j2 server frontend

server:
	gradle run

frontend:
	cd web && npm run dev

install:
	cd web && npm install

build:
	cd web && npm run build
