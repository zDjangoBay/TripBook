// NotificationListFragment.kt
class NotificationListFragment : Fragment() {
    
    private var _binding: FragmentNotificationListBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: NotificationViewModel by viewModels {
        NotificationViewModelFactory(
            NotificationRepository(
                NotificationDatabase.getDatabase(requireContext()).notificationDao()
            )
        )
    }
    
    private lateinit var adapter: NotificationAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationListBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        setupObservers()
        setupClickListeners()
    }
    
    private fun setupRecyclerView() {
        adapter = NotificationAdapter(
            onItemClick = { notification ->
                if (!notification.isRead) {
                    viewModel.markAsRead(notification.id)
                }
                findNavController().navigate(
                    NotificationListFragmentDirections
                        .actionNotificationListToNotificationDetail(notification.id)
                )
            },
            onDeleteClick = { notification ->
                viewModel.deleteNotification(notification)
            }
        )
        
        binding.recyclerViewNotifications.apply {
            this.adapter = this@NotificationListFragment.adapter
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(
                DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
            )
        }
    }
    
    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is NotificationUIState.Loading -> {
                        binding.progressBar.isVisible = true
                        binding.recyclerViewNotifications.isVisible = false
                        binding.layoutEmpty.isVisible = false
                    }
                    is NotificationUIState.Success -> {
                        binding.progressBar.isVisible = false
                        binding.recyclerViewNotifications.isVisible = true
                        binding.layoutEmpty.isVisible = false
                        adapter.submitList(state.notifications)
                    }
                    is NotificationUIState.Empty -> {
                        binding.progressBar.isVisible = false
                        binding.recyclerViewNotifications.isVisible = false
                        binding.layoutEmpty.isVisible = true
                    }
                    is NotificationUIState.Error -> {
                        binding.progressBar.isVisible = false
                        Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG).show()
                    }
                }
            }
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.unreadCount.collect { count ->
                binding.textUnreadCount.apply {
                    text = count.toString()
                    isVisible = count > 0
                }
            }
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.selectedFilter.collect { filter ->
                updateFilterButtons(filter)
            }
        }
    }
    
    private fun setupClickListeners() {
        binding.apply {
            buttonFilterAll.setOnClickListener { viewModel.setFilter("all") }
            buttonFilterUnread.setOnClickListener { viewModel.setFilter("unread") }
            buttonFilterRead.setOnClickListener { viewModel.setFilter("read") }
            buttonMarkAllRead.setOnClickListener { viewModel.markAllAsRead() }
            buttonAddDemo.setOnClickListener { viewModel.createDemoNotification() }
        }
    }
    
    private fun updateFilterButtons(selectedFilter: String) {
        binding.apply {
            buttonFilterAll.isSelected = selectedFilter == "all"
            buttonFilterUnread.isSelected = selectedFilter == "unread"
            buttonFilterRead.isSelected = selectedFilter == "read"
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

// NotificationDetailFragment.kt
class NotificationDetailFragment : Fragment() {
    
    private var _binding: FragmentNotificationDetailBinding? = null
    private val binding get() = _binding!!
    
    private val args: NotificationDetailFragmentArgs by navArgs()
    
    private val viewModel: NotificationViewModel by viewModels {
        NotificationViewModelFactory(
            NotificationRepository(
                NotificationDatabase.getDatabase(requireContext()).notificationDao()
            )
        )
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationDetailBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupToolbar()
        loadNotificationDetails()
    }
    
    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }
    
    private fun loadNotificationDetails() {
        viewLifecycleOwner.lifecycleScope.launch {
            val notification = viewModel.getNotificationById(args.notificationId)
            notification?.let { displayNotification(it) }
        }
    }
    
    private fun displayNotification(notification: Notification) {
        binding.apply {
            textTitle.text = notification.title
            textMessage.text = notification.message
            textTimestamp.text = notification.timestamp.formatFullTimestamp()
            
            // Type indicator
            val (colorRes, iconRes) = when (notification.type) {
                NotificationType.SUCCESS -> R.color.green_500 to R.drawable.ic_check_circle
                NotificationType.WARNING -> R.color.orange_500 to R.drawable.ic_warning
                NotificationType.ERROR -> R.color.red_500 to R.drawable.ic_error
                NotificationType.INFO -> R.color.blue_500 to R.drawable.ic_info
            }
            
            imageType.setImageResource(iconRes)
            imageType.setColorFilter(ContextCompat.getColor(requireContext(), colorRes))
            
            // Metadata
            notification.metadata?.let { metadata ->
                textSender.text = "From: ${metadata.sender}"
                textCategory.text = "Category: ${metadata.category}"
                
                textSender.isVisible = !metadata.sender.isNullOrEmpty()
                textCategory.isVisible = !metadata.category.isNullOrEmpty()
                
                metadata.actionUrl?.let { url ->
                    buttonAction.isVisible = true
                    buttonAction.setOnClickListener {
                        // Handle action URL (e.g., open browser)
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        startActivity(intent)
                    }
                }
            } ?: run {
                textSender.isVisible = false
                textCategory.isVisible = false
                buttonAction.isVisible = false
            }
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
