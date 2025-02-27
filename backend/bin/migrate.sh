#!/run/current-system/sw/bin/bash
read -p "Migration Name: " query
if [[ -n "$query" ]]; then  
  alembic revision --autogenerate -m "$query" #Generate migration script
  alembic upgrade head #apply migration
  alembic history
fi
