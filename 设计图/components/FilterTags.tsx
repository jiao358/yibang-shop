export function FilterTags() {
  const tags = [
    { name: '推荐', isActive: true },
    { name: '关注', isActive: false },
    { name: '女装', isActive: false },
    { name: '男装', isActive: false },
    { name: '鞋靴', isActive: false }
  ]

  return (
    <div className="flex items-center gap-3 px-4 py-3 overflow-x-auto">
      {tags.map((tag, index) => (
        <button
          key={index}
          className={`px-4 py-1 rounded-full text-sm whitespace-nowrap ${
            tag.isActive
              ? 'bg-red-50 text-red-500 border border-red-200'
              : 'bg-gray-100 text-gray-600'
          }`}
        >
          {tag.name}
        </button>
      ))}
    </div>
  )
}