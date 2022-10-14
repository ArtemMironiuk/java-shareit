package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.handler.exception.ObjectNotFoundException;
import ru.practicum.shareit.handler.exception.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    @Autowired
    private ItemRequestRepository itemRequestRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;

    @Override
    public ItemRequestDtoOutput createRequest(Long userId, ItemRequestDtoInput requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new ObjectNotFoundException("User не зарегистрирован"));
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(requestDto, user);
        return ItemRequestMapper.toItemRequestDtoOutput(itemRequestRepository.save(itemRequest));
    }

    @Override
    public List<RequestAndResponseDtoOut> findRequestsOfOwner(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new ObjectNotFoundException("User не зарегистрирован"));
        Sort createdDesc = Sort.by(Sort.Direction.DESC, "created");
        List<RequestAndResponseDtoOut> resultRequestAndResponse = new ArrayList<>();
        List<ItemRequest> requestList = itemRequestRepository.findByRequesterId(userId, createdDesc);
        for (ItemRequest request : requestList) {
            List<Item> itemList = itemRepository.findByRequest_Id(request.getId());
            resultRequestAndResponse.add(ItemRequestMapper.toRequestAndResponseDtoOut(request, itemList));
        }
        return resultRequestAndResponse;
    }

    @Override
    public List<RequestAndResponseDtoOut> findRequestsOfUser(Long userId, Integer from, Integer size) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new ObjectNotFoundException("User не зарегистрирован"));
        if (from < 0) {
            throw new ValidationException("from меньше 0");
        }
        if (size<=0) {
            throw new ValidationException("size меньше 0");
        }
        Sort createdDesc = Sort.by(Sort.Direction.DESC, "created");
        Pageable pageable = PageRequest.of(from, size, createdDesc);
        List<RequestAndResponseDtoOut> resultRequestAndResponse = new ArrayList<>();
        Page<ItemRequest> requestList = itemRequestRepository.findAll(pageable);
        for (ItemRequest request : requestList) {
            List<Item> itemList = itemRepository.findByRequest_Id(request.getId());
            resultRequestAndResponse.add(ItemRequestMapper.toRequestAndResponseDtoOut(request, itemList));
        }
        return resultRequestAndResponse;
    }

    @Override
    public RequestAndResponseDtoOut findRequest(Long userId, Long requestId) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new ObjectNotFoundException("User не зарегистрирован"));
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(()->new ObjectNotFoundException("Ничего не найдено по requestId = {}", requestId));
        List<Item> itemList = itemRepository.findByRequest_Id(itemRequest.getId());
        return ItemRequestMapper.toRequestAndResponseDtoOut(itemRequest, itemList);
    }
}
