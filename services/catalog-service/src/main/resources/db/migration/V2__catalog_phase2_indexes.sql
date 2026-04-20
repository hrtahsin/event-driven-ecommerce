CREATE UNIQUE INDEX IF NOT EXISTS ux_products_sku_lower ON products ((LOWER(sku)));

CREATE UNIQUE INDEX IF NOT EXISTS ux_categories_name_lower ON categories ((LOWER(name)));

CREATE INDEX IF NOT EXISTS idx_products_active ON products (active);

CREATE INDEX IF NOT EXISTS idx_products_name_lower ON products ((LOWER(name)));
