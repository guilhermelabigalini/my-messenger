using my.messenger.common.Messaging;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace my.messenger.common.Users
{
    public class GroupService
    {
        private const int MaxMembers = 50;

        private IUserRepository userRepository;
        private IGroupRepository groupRepository;
        private IMessageSender messageSender;

        public GroupService(
            IUserRepository userRepository,
            IGroupRepository groupRepository,
            IMessageSender messageSender)
        {
            this.userRepository = userRepository;
            this.groupRepository = groupRepository;
            this.messageSender = messageSender;
        }

        public Task<Group> GetAsync(String groupId)
        {
            return groupRepository.FindByIdAsync(groupId);
        }

        public async Task<Group> RegisterAsync(String name, UserSession loggedUserSession)
        {
            Group group = new Group();
            group.Name = name;
            group.OwnerUserId = loggedUserSession.UserId;
            group.Members = new List<string>()
            {
                loggedUserSession.UserId
            };

            if (!StringUtils.hasText(group.Name))
            {
                throw new ValidationException("Name is required");
            }
            
            await groupRepository.InsertAsync(group);

            // register the topic! 
            Destination destinationGroup = new Destination(DestinationType.Group, group.Id);
            await messageSender.RegisterDestinationAsync(destinationGroup);

            // register the owner as member
            await messageSender.RegisterDestinationListenerAsync(
                new Destination(DestinationType.Group, group.Id),
                new Destination(DestinationType.User, loggedUserSession.UserId));

            return group;
        }

        public async Task AddMemberAsync(GroupMemberChangeRequest request, UserSession loggedUserSession)
        {
            ValidateRequest(request);

            Group gp = await groupRepository.FindByIdAsync(request.GroupId);
            
            if (! gp.OwnerUserId.Equals(loggedUserSession.UserId, StringComparison.InvariantCultureIgnoreCase))
            {
                throw new ValidationException("Access denied");
            }

            if (gp.Members != null && gp.Members.Count >= MaxMembers)
            {
                throw new ValidationException("Members has exceeed");
            }

            var dbUser = await userRepository.FindByIdAsync(request.MemberUserId);
            if (dbUser == null)
            {
                throw new ValidationException("invalid member id");
            }

            if (! gp.Members.Any(i => i.Equals(request.MemberUserId, StringComparison.InvariantCultureIgnoreCase)))
            {
                await groupRepository.AddMemberAsync(gp.Id, request.MemberUserId);

                await messageSender.RegisterDestinationListenerAsync(
                    new Destination(DestinationType.Group, gp.Id),
                    new Destination(DestinationType.User, request.MemberUserId));
            }
        }

        private void ValidateRequest(GroupMemberChangeRequest request)
        {
            if (String.IsNullOrEmpty(request.GroupId))
            {
                throw new ValidationException("Invaid GroupId");
            }

            if (String.IsNullOrEmpty(request.MemberUserId))
            {
                throw new ValidationException("Invaid MemberUserId");
            }
        }

        public async Task RemoveMemberAsync(GroupMemberChangeRequest request, UserSession loggedUserSession)
        {
            ValidateRequest(request);

            Group gp = await groupRepository.FindByIdAsync(request.GroupId);

            bool canEdit = (gp.OwnerUserId.Equals(loggedUserSession.UserId, StringComparison.InvariantCultureIgnoreCase)
                || loggedUserSession.UserId.Equals(request.MemberUserId, StringComparison.InvariantCultureIgnoreCase));

            // only the owner can remove users or the user can remove him self
            if ( !canEdit)
            {
                throw new ValidationException("Access denied");
            }

            if (gp.OwnerUserId.Equals(request.MemberUserId, StringComparison.InvariantCultureIgnoreCase))
            {
                throw new ValidationException("Unable to remove owner");
            }

            var dbUser = await userRepository.FindByIdAsync(request.MemberUserId);
            if (dbUser == null)
            {
                throw new ValidationException("invalid member id");
            }

            if (gp.Members.Any(i => i.Equals(request.MemberUserId, StringComparison.InvariantCultureIgnoreCase)))
            {
                await groupRepository.RemoveMemberAsync(request.GroupId, request.MemberUserId);

                await messageSender.RemoveDestinationListenerAsync(
                    new Destination(DestinationType.Group, gp.Id),
                    new Destination(DestinationType.User, request.MemberUserId));
            }
        }
    }
}
