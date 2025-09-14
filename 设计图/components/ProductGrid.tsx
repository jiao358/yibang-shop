import { ImageWithFallback } from './figma/ImageWithFallback'

export function ProductGrid() {
  const products = [
    {
      id: 1,
      title: '撕拉抗皱睡眠膜',
      subtitle: '撕拉抗皱睡眠',
      price: 39,
      originalPrice: 99,
      image: 'https://images.unsplash.com/photo-1644641811682-0439fd0185d0?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxza2luY2FyZSUyMGJlYXV0eSUyMHByb2R1Y3R8ZW58MXx8fHwxNzU3Nzg5NjU0fDA&ixlib=rb-4.1.0&q=80&w=1080&utm_source=figma&utm_medium=referral',
      tag: '限时'
    },
    {
      id: 2,
      title: 'HelloKitty 贝壳包挂件',
      price: 29,
      originalPrice: 99,
      image: 'https://images.unsplash.com/photo-1663223960481-90771f8a849d?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxmYXNoaW9uJTIwYWNjZXNzb3JpZXMlMjBoYW5kYmFnfGVufDF8fHx8MTc1Nzg0NDg2Nnww&ixlib=rb-4.1.0&q=80&w=1080&utm_source=figma&utm_medium=referral',
      tag: '限时'
    },
    {
      id: 3,
      title: '秋冬保暖加绒手套赠拍',
      price: 26,
      originalPrice: 99,
      image: 'https://images.unsplash.com/photo-1639654827521-cb4f5edf8675?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxnbG92ZXMlMjB3aW50ZXIlMjBhY2Nlc3Nvcmllc3xlbnwxfHx8fDE3NTc4NDQ4Njd8MA&ixlib=rb-4.1.0&q=80&w=1080&utm_source=figma&utm_medium=referral',
      tag: '赠拍',
      notification: '订单通知'
    }
  ]

  const hotItems = [
    {
      id: 4,
      title: '颜拉除湿器体验',
      subtitle: '高速过渡',
      image: 'https://images.unsplash.com/photo-1730299788623-12cded84cf40?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxob21lJTIwYXBwbGlhbmNlJTIwaHVtaWRpZmllcnxlbnwxfHx8fDE3NTc4NDQ4Njd8MA&ixlib=rb-4.1.0&q=80&w=1080&utm_source=figma&utm_medium=referral'
    },
    {
      id: 5,
      title: '创享好物',
      subtitle: '玄关收纳架',
      image: 'https://images.unsplash.com/photo-1663223960481-90771f8a849d?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxmYXNoaW9uJTIwYWNjZXNzb3JpZXMlMjBoYW5kYmFnfGVufDF8fHx8MTc1Nzg0NDg2Nnww&ixlib=rb-4.1.0&q=80&w=1080&utm_source=figma&utm_medium=referral'
    },
    {
      id: 6,
      title: '草编收纳篮',
      subtitle: '置物篮',
      image: 'https://images.unsplash.com/photo-1639654827521-cb4f5edf8675?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxnbG92ZXMlMjB3aW50ZXIlMjBhY2Nlc3Nvcmllc3xlbnwxfHx8fDE3NTc4NDQ4Njd8MA&ixlib=rb-4.1.0&q=80&w=1080&utm_source=figma&utm_medium=referral'
    }
  ]

  return (
    <div className="px-4 space-y-4">
      {/* Main product card */}
      <div className="bg-white rounded-lg overflow-hidden shadow-sm">
        <div className="relative">
          <ImageWithFallback 
            src={products[0].image}
            alt={products[0].title}
            className="w-full h-48 object-cover"
          />
          <div className="absolute top-2 left-2 bg-orange-400 text-white px-2 py-1 rounded text-xs">
            {products[0].tag}
          </div>
        </div>
        <div className="p-3">
          <h3 className="text-sm text-gray-800 mb-1">{products[0].title}</h3>
          <p className="text-xs text-gray-500 mb-2">{products[0].subtitle}</p>
          <div className="flex items-center gap-2">
            <span className="text-orange-500">¥ {products[0].price}</span>
            <span className="text-gray-400 line-through text-xs">¥{products[0].originalPrice}+</span>
          </div>
        </div>
      </div>

      {/* Hot activity section */}
      <div className="bg-gradient-to-r from-orange-50 to-red-50 rounded-lg p-4">
        <div className="flex items-center justify-between mb-3">
          <div className="flex items-center gap-2">
            <span className="text-orange-500">🔥</span>
            <span className="text-orange-600">热门活动</span>
          </div>
          <button className="text-orange-500 text-sm">加厂</button>
        </div>
        
        <div className="grid grid-cols-2 gap-3 mb-3">
          {hotItems.slice(0, 2).map((item) => (
            <div key={item.id} className="bg-white rounded-lg overflow-hidden">
              <ImageWithFallback 
                src={item.image}
                alt={item.title}
                className="w-full h-20 object-cover"
              />
              <div className="p-2">
                <p className="text-xs text-gray-700">{item.title}</p>
                <p className="text-xs text-gray-500">{item.subtitle}</p>
              </div>
            </div>
          ))}
        </div>
        
        <div className="grid grid-cols-2 gap-3">
          <button className="bg-orange-400 text-white py-2 rounded text-sm">
            戳级体量
          </button>
          <button className="bg-orange-400 text-white py-2 rounded text-sm">
            别享好物
          </button>
        </div>
      </div>

      {/* Bottom product grid */}
      <div className="grid grid-cols-2 gap-4">
        {products.slice(1).map((product) => (
          <div key={product.id} className="bg-white rounded-lg overflow-hidden shadow-sm">
            <div className="relative">
              <ImageWithFallback 
                src={product.image}
                alt={product.title}
                className="w-full h-32 object-cover"
              />
              <div className="absolute top-2 left-2 bg-orange-400 text-white px-2 py-1 rounded text-xs">
                {product.tag}
              </div>
              {product.notification && (
                <div className="absolute bottom-2 right-2 bg-red-500 text-white px-2 py-1 rounded-full text-xs">
                  {product.notification}
                </div>
              )}
            </div>
            <div className="p-2">
              <h3 className="text-xs text-gray-800 mb-1">{product.title}</h3>
              <div className="flex items-center gap-1">
                <span className="text-orange-500 text-sm">¥ {product.price}</span>
                <span className="text-gray-400 line-through text-xs">¥{product.originalPrice}+</span>
              </div>
              <p className="text-xs text-gray-400 mt-1">3分钟前</p>
            </div>
          </div>
        ))}
      </div>
    </div>
  )
}