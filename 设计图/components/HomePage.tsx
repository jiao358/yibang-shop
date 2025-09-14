import { Header } from './Header'
import { PromoBanner } from './PromoBanner'
import { CategoryNav } from './CategoryNav'
import { FilterTags } from './FilterTags'
import { ProductGrid } from './ProductGrid'

export function HomePage() {
  return (
    <>
      <Header />
      <PromoBanner />
      <CategoryNav />
      <FilterTags />
      <ProductGrid />
    </>
  )
}