export function CategoryNav() {
  const categories = [
    { name: '全部', isActive: true },
    { name: '网拍', isActive: false },
    { name: '种草', isActive: false },
    { name: '陪拍', isActive: false },
    { name: '奇拍', isActive: false },
    { name: '筛选', isActive: false, hasIcon: true }
  ]

  return (
    <div className="flex items-center justify-between px-4 py-2 border-b border-gray-100">
      {categories.map((category, index) => (
        <button
          key={index}
          className={`px-3 py-1 text-sm ${
            category.isActive 
              ? 'text-red-500 border-b-2 border-red-500' 
              : 'text-gray-600'
          }`}
        >
          {category.name}
          {category.hasIcon && (
            <span className="ml-1">⚙️</span>
          )}
        </button>
      ))}
    </div>
  )
}